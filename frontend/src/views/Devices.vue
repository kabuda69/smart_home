<template>
  <!-- 设备管理页面 -->
  <div class="devices-page">
    <!-- 工具栏 -->
    <div class="page-toolbar">
      <button class="add-btn" @click="showAddDialog = true">
        <span class="mdi mdi-plus"></span> 添加设备
      </button>
    </div>

    <!-- 空状态 -->
    <div v-if="devices.length === 0" class="empty-card">
      <span class="mdi mdi-devices"></span>
      <h3>暂无设备</h3>
      <p>点击上方按钮添加您的第一个智能设备</p>
    </div>

    <!-- 设备网格 -->
    <div v-else class="devices-grid">
      <div v-for="device in devices" :key="device.id" class="device-card" :class="{ online: device.status === 'online' }">
        <!-- 设备顶部信息 -->
        <div class="card-top">
          <div class="device-icon" :class="{ on: device.powerState }">
            <span :class="'mdi ' + getIcon(device.typeName)"></span>
          </div>
          <div class="device-info">
            <h4>{{ device.name }}</h4>
            <span class="type-tag">{{ device.typeName }}</span>
          </div>
          <span class="status-dot" :class="device.status"></span>
        </div>

        <!-- 设备控制区域 -->
        <div class="card-body">
          <!-- 电源开关 -->
          <div class="control-item">
            <span>电源开关</span>
            <label class="switch">
              <input type="checkbox" :checked="device.powerState" @change="togglePower(device)">
              <span class="slider"></span>
            </label>
          </div>

          <!-- 数值调节（适用于灯、空调、窗帘等） -->
          <div v-if="hasValueControl(device.typeName)" class="control-item column">
            <div class="slider-label">
              <span>数值调节</span>
              <span class="value">{{ sliderValues[device.id] || 0 }}</span>
            </div>
            <input type="range" v-model="sliderValues[device.id]" min="0" max="100" :disabled="!device.powerState" @change="setValue(device)" class="range-slider">
          </div>

          <!-- 传感器数据显示 -->
          <div v-else-if="isSensor(device.typeName) && device.powerState" class="sensor-display">
            <span class="sensor-val">{{ device.currentValue || 0 }}</span>
            <span class="sensor-unit">{{ getSensorUnit(device.typeName) }}</span>
            <button class="sim-btn" @click="simulateSensor(device)">
              <span class="mdi mdi-refresh"></span> 模拟数据
            </button>
          </div>

          <!-- 阈值信息 -->
          <div v-if="device.thresholdMin !== null" class="threshold-info">
            <span class="mdi mdi-alert-circle-outline"></span>
            阈值范围: {{ device.thresholdMin }} - {{ device.thresholdMax }}
          </div>
        </div>

        <!-- 设备操作按钮 -->
        <div class="card-actions">
          <button @click="openThresholdDialog(device)" title="设置阈值"><span class="mdi mdi-tune"></span></button>
          <button @click="openHistoryDialog(device)" title="历史记录"><span class="mdi mdi-history"></span></button>
          <button @click="shareDevice(device)" title="分享"><span class="mdi mdi-share-variant"></span></button>
          <button class="danger" @click="deleteDevice(device.id)" title="删除"><span class="mdi mdi-delete"></span></button>
        </div>
      </div>
    </div>

    <!-- 添加设备对话框 -->
    <v-dialog v-model="showAddDialog" max-width="450">
      <div class="dialog-box">
        <div class="dialog-header">
          <h3>添加设备</h3>
          <button class="close-btn" @click="showAddDialog = false"><span class="mdi mdi-close"></span></button>
        </div>
        <div class="dialog-body">
          <div class="field"><label>设备名称</label><input v-model="newDevice.name" type="text" placeholder="如: 客厅灯"></div>
          <div class="field"><label>设备类型</label><select v-model="newDevice.typeId"><option value="">请选择</option><option v-for="t in deviceTypes" :key="t.id" :value="t.id">{{ t.name }}</option></select></div>
          <div class="field-row">
            <div class="field"><label>最小阈值</label><input v-model.number="newDevice.thresholdMin" type="number" placeholder="可选"></div>
            <div class="field"><label>最大阈值</label><input v-model.number="newDevice.thresholdMax" type="number" placeholder="可选"></div>
          </div>
        </div>
        <div class="dialog-footer">
          <button class="btn-cancel" @click="showAddDialog = false">取消</button>
          <button class="btn-confirm" :disabled="!newDevice.name || !newDevice.typeId" @click="addDevice">添加</button>
        </div>
      </div>
    </v-dialog>

    <!-- 设置阈值对话框 -->
    <v-dialog v-model="showThresholdDialog" max-width="380">
      <div class="dialog-box">
        <div class="dialog-header"><h3>设置阈值</h3><button class="close-btn" @click="showThresholdDialog = false"><span class="mdi mdi-close"></span></button></div>
        <div class="dialog-body">
          <p class="dialog-subtitle">{{ thresholdForm.deviceName }}</p>
          <div class="field"><label>最小阈值</label><input v-model.number="thresholdForm.min" type="number"></div>
          <div class="field"><label>最大阈值</label><input v-model.number="thresholdForm.max" type="number"></div>
        </div>
        <div class="dialog-footer"><button class="btn-cancel" @click="showThresholdDialog = false">取消</button><button class="btn-confirm" @click="setThreshold">保存</button></div>
      </div>
    </v-dialog>

    <!-- 历史记录对话框 -->
    <v-dialog v-model="showHistoryDialog" max-width="500">
      <div class="dialog-box">
        <div class="dialog-header"><h3>历史记录</h3><button class="close-btn" @click="showHistoryDialog = false"><span class="mdi mdi-close"></span></button></div>
        <div class="dialog-body">
          <table class="data-table">
            <thead><tr><th>数值</th><th>电源</th><th>时间</th></tr></thead>
            <tbody>
              <tr v-for="h in historyData" :key="h.id">
                <td>{{ h.statusValue !== null ? h.statusValue : '-' }}</td>
                <td><span class="tag" :class="h.powerState ? 'on' : 'off'">{{ h.powerState ? '开' : '关' }}</span></td>
                <td>{{ formatDate(h.recordedAt) }}</td>
              </tr>
              <tr v-if="historyData.length === 0"><td colspan="3" class="empty-cell">暂无数据</td></tr>
            </tbody>
          </table>
        </div>
      </div>
    </v-dialog>

    <!-- 消息提示 -->
    <v-snackbar v-model="snackbar.show" :color="snackbar.color" timeout="2000">{{ snackbar.text }}</v-snackbar>
  </div>
</template>

<script>
import { mapState } from 'vuex'

/**
 * 设备管理组件
 * 用于管理智能设备的添加、控制、设置等操作
 */
export default {
  /**
   * 组件数据
   */
  data: function() {
    return {
      showAddDialog: false, // 添加设备对话框
      showThresholdDialog: false, // 设置阈值对话框
      showHistoryDialog: false, // 历史记录对话框
      snackbar: { show: false, text: '', color: 'success' }, // 消息提示
      deviceTypes: [], // 设备类型列表
      newDevice: { name: '', typeId: null, thresholdMin: null, thresholdMax: null }, // 新设备表单
      thresholdForm: { deviceId: null, deviceName: '', min: 0, max: 100 }, // 阈值表单
      sliderValues: {}, // 滑块值
      historyData: [] // 历史记录数据
    }
  },
  
  /**
   * 计算属性
   */
  computed: { 
    ...mapState(['devices']) // 从Vuex获取设备列表
  },
  
  /**
   * 监听器
   */
  watch: { 
    // 监听设备列表变化，更新滑块值
    devices: { 
      handler: function(d) { 
        var self = this; 
        d.forEach(function(x) { 
          self.$set(self.sliderValues, x.id, x.currentValue || 0) 
        }) 
      }, 
      immediate: true, 
      deep: true 
    } 
  },
  
  /**
   * 组件挂载时执行
   */
  mounted: function() { 
    var self = this; 
    // 获取设备列表
    this.$store.dispatch('fetchDevices'); 
    // 获取设备类型列表
    this.$axios.get('/api/device-types').then(function(r) { 
      if (r.data.success) self.deviceTypes = r.data.data 
    }) 
  },
  
  /**
   * 组件方法
   */
  methods: {
    /**
     * 获取设备图标
     * @param {string} t 设备类型名称
     * @returns {string} 图标类名
     */
    getIcon: function(t) { 
      var m = { 
        '智能灯': 'mdi-lightbulb', 
        '空调': 'mdi-air-conditioner', 
        '温度传感器': 'mdi-thermometer', 
        '湿度传感器': 'mdi-water-percent', 
        '智能插座': 'mdi-power-socket', 
        '门窗传感器': 'mdi-door', 
        '烟雾报警器': 'mdi-smoke-detector', 
        '智能窗帘': 'mdi-blinds' 
      }; 
      return m[t] || 'mdi-devices' 
    },
    
    /**
     * 检查设备是否支持数值调节
     * @param {string} t 设备类型名称
     * @returns {boolean} 是否支持
     */
    hasValueControl: function(t) { 
      return ['智能灯', '空调', '智能窗帘'].indexOf(t) !== -1 
    },
    
    /**
     * 检查设备是否为传感器
     * @param {string} t 设备类型名称
     * @returns {boolean} 是否为传感器
     */
    isSensor: function(t) { 
      return ['温度传感器', '湿度传感器', '烟雾报警器'].indexOf(t) !== -1 
    },
    
    /**
     * 获取传感器单位
     * @param {string} t 设备类型名称
     * @returns {string} 单位
     */
    getSensorUnit: function(t) { 
      return { 
        '温度传感器': '°C', 
        '湿度传感器': '%', 
        '烟雾报警器': 'ppm' 
      }[t] || '' 
    },
    
    /**
     * 模拟传感器数据
     * @param {Object} d 设备对象
     */
    simulateSensor: function(d) { 
      var self = this, v = Math.floor(Math.random() * 30) + 15; 
      this.$axios.post('/api/devices/command', { 
        deviceId: d.id, 
        commandType: 'SET_VALUE', 
        commandValue: String(v) 
      }).then(function() { 
        self.$store.dispatch('fetchDevices'); 
        self.showMsg('模拟数据: ' + v, 'success') 
      }) 
    },
    
    /**
     * 切换设备电源状态
     * @param {Object} d 设备对象
     */
    togglePower: function(d) { 
      var self = this, cmd = d.powerState ? 'POWER_OFF' : 'POWER_ON'; 
      this.$axios.post('/api/devices/command', { 
        deviceId: d.id, 
        commandType: cmd 
      }).then(function() { 
        self.$store.dispatch('fetchDevices'); 
        self.showMsg(cmd === 'POWER_ON' ? '已开启' : '已关闭', 'success') 
      }) 
    },
    
    /**
     * 设置设备数值
     * @param {Object} d 设备对象
     */
    setValue: function(d) { 
      var self = this, v = this.sliderValues[d.id]; 
      this.$axios.post('/api/devices/command', { 
        deviceId: d.id, 
        commandType: 'SET_VALUE', 
        commandValue: String(v) 
      }).then(function() { 
        self.$store.dispatch('fetchDevices'); 
        self.showMsg('已设置: ' + v, 'success') 
      }) 
    },
    
    /**
     * 添加设备
     */
    addDevice: function() { 
      var self = this; 
      this.$axios.post('/api/devices', this.newDevice).then(function(r) { 
        if (r.data.success) { 
          self.showAddDialog = false; 
          self.newDevice = { name: '', typeId: null, thresholdMin: null, thresholdMax: null }; 
          self.$store.dispatch('fetchDevices'); 
          self.showMsg('添加成功', 'success') 
        } 
      }) 
    },
    
    /**
     * 删除设备
     * @param {number} id 设备ID
     */
    deleteDevice: function(id) { 
      var self = this; 
      if (!confirm('确定删除？')) return; 
      this.$axios.delete('/api/devices/' + id).then(function() { 
        self.$store.dispatch('fetchDevices'); 
        self.showMsg('已删除', 'success') 
      }) 
    },
    
    /**
     * 打开阈值设置对话框
     * @param {Object} d 设备对象
     */
    openThresholdDialog: function(d) { 
      this.thresholdForm = { 
        deviceId: d.id, 
        deviceName: d.name, 
        min: d.thresholdMin || 0, 
        max: d.thresholdMax || 100 
      }; 
      this.showThresholdDialog = true 
    },
    
    /**
     * 设置阈值
     */
    setThreshold: function() { 
      var self = this; 
      this.$axios.post('/api/devices/command', { 
        deviceId: this.thresholdForm.deviceId, 
        commandType: 'SET_THRESHOLD', 
        commandValue: this.thresholdForm.min + ',' + this.thresholdForm.max 
      }).then(function() { 
        self.showThresholdDialog = false; 
        self.$store.dispatch('fetchDevices'); 
        self.showMsg('已设置', 'success') 
      }) 
    },
    
    /**
     * 打开历史记录对话框
     * @param {Object} d 设备对象
     */
    openHistoryDialog: function(d) { 
      var self = this; 
      this.historyData = []; 
      this.showHistoryDialog = true; 
      this.$axios.get('/api/devices/' + d.id + '/history', { 
        params: { page: 0, size: 10 } 
      }).then(function(r) { 
        if (r.data.success) self.historyData = r.data.data.content 
      }) 
    },
    
    /**
     * 格式化日期
     * @param {string} d 日期字符串
     * @returns {string} 格式化后的日期
     */
    formatDate: function(d) { 
      return d ? new Date(d).toLocaleString('zh-CN') : '-' 
    },
    
    /**
     * 分享设备
     * @param {Object} d 设备对象
     */
    shareDevice: function(d) { 
      var self = this; 
      this.$axios.post('/api/share', { 
        deviceId: d.id, 
        expireHours: 24 
      }).then(function(r) { 
        if (r.data.success) { 
          navigator.clipboard.writeText(window.location.origin + '/share/' + r.data.data); 
          self.showMsg('链接已复制', 'success') 
        } 
      }) 
    },
    
    /**
     * 显示消息
     * @param {string} t 消息文本
     * @param {string} c 消息颜色
     */
    showMsg: function(t, c) { 
      this.snackbar = { show: true, text: t, color: c } 
    }
  }
}
</script>

<style scoped>
/* 设备页面样式 */
.devices-page { padding: 0 32px 32px; max-width: 1400px; margin: 0 auto; }
.page-toolbar { display: flex; justify-content: flex-end; margin-bottom: 20px; }
.add-btn { display: flex; align-items: center; gap: 6px; padding: 10px 20px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); border: none; border-radius: 8px; color: #fff; font-size: 14px; cursor: pointer; }

/* 空状态样式 */
.empty-card { background: #fff; border-radius: 12px; padding: 60px; text-align: center; }
.empty-card .mdi { font-size: 60px; color: #ddd; }
.empty-card h3 { font-size: 18px; color: #333; margin: 16px 0 8px; }
.empty-card p { color: #888; }

/* 设备网格样式 */
.devices-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); gap: 20px; }
.device-card { background: #fff; border-radius: 12px; overflow: hidden; border: 2px solid transparent; transition: all 0.2s; }
.device-card:hover { box-shadow: 0 4px 20px rgba(0,0,0,0.08); }
.device-card.online { border-color: #4caf50; }

/* 设备卡片样式 */
.card-top { display: flex; align-items: center; gap: 12px; padding: 16px; }
.device-icon { width: 48px; height: 48px; background: #f5f5f5; border-radius: 10px; display: flex; align-items: center; justify-content: center; }
.device-icon .mdi { font-size: 24px; color: #666; }
.device-icon.on { background: #4caf50; }
.device-icon.on .mdi { color: #fff; }
.device-info { flex: 1; }
.device-info h4 { font-size: 15px; color: #333; margin-bottom: 2px; }
.type-tag { font-size: 12px; color: #888; }
.status-dot { width: 10px; height: 10px; border-radius: 50%; background: #ddd; }
.status-dot.online { background: #4caf50; }

/* 卡片内容样式 */
.card-body { padding: 0 16px 16px; }
.control-item { display: flex; justify-content: space-between; align-items: center; padding: 12px 0; border-bottom: 1px solid #f0f0f0; font-size: 14px; color: #666; }
.control-item:last-child { border-bottom: none; }
.control-item.column { flex-direction: column; align-items: stretch; gap: 10px; }
.slider-label { display: flex; justify-content: space-between; }
.slider-label .value { font-weight: 600; color: #667eea; }
.range-slider { width: 100%; height: 6px; border-radius: 3px; background: #e0e0e0; -webkit-appearance: none; }
.range-slider::-webkit-slider-thumb { -webkit-appearance: none; width: 16px; height: 16px; border-radius: 50%; background: #667eea; cursor: pointer; }

/* 传感器显示样式 */
.sensor-display { background: #e3f2fd; border-radius: 10px; padding: 16px; text-align: center; }
.sensor-val { font-size: 32px; font-weight: 700; color: #1976d2; }
.sensor-unit { font-size: 14px; color: #666; }
.sim-btn { margin-top: 10px; padding: 6px 14px; background: #fff; border: none; border-radius: 6px; color: #1976d2; font-size: 12px; cursor: pointer; display: inline-flex; align-items: center; gap: 4px; }

/* 阈值信息样式 */
.threshold-info { display: flex; align-items: center; gap: 6px; font-size: 12px; color: #888; padding-top: 10px; }

/* 卡片操作按钮样式 */
.card-actions { display: flex; gap: 8px; padding: 12px 16px; background: #fafafa; }
.card-actions button { width: 34px; height: 34px; border: none; background: #fff; border-radius: 8px; color: #666; cursor: pointer; display: flex; align-items: center; justify-content: center; }
.card-actions button:hover { background: #667eea; color: #fff; }
.card-actions button.danger:hover { background: #e53935; }

/* 开关样式 */
.switch { position: relative; width: 44px; height: 24px; }
.switch input { opacity: 0; width: 0; height: 0; }
.slider { position: absolute; cursor: pointer; inset: 0; background: #ccc; border-radius: 24px; transition: 0.3s; }
.slider:before { content: ''; position: absolute; width: 18px; height: 18px; left: 3px; bottom: 3px; background: #fff; border-radius: 50%; transition: 0.3s; }
.switch input:checked + .slider { background: #4caf50; }
.switch input:checked + .slider:before { transform: translateX(20px); }

/* 对话框样式 */
.dialog-box { background: #fff; border-radius: 12px; overflow: hidden; }
.dialog-header { display: flex; justify-content: space-between; align-items: center; padding: 18px 20px; border-bottom: 1px solid #f0f0f0; }
.dialog-header h3 { font-size: 16px; color: #333; margin: 0; }
.close-btn { width: 30px; height: 30px; border: none; background: #f5f5f5; border-radius: 6px; color: #666; cursor: pointer; }
.dialog-body { padding: 20px; }
.dialog-subtitle { color: #888; margin-bottom: 16px; }
.field { margin-bottom: 16px; }
.field label { display: block; font-size: 13px; color: #333; margin-bottom: 6px; }
.field input, .field select { width: 100%; padding: 10px 12px; border: 1px solid #ddd; border-radius: 8px; font-size: 14px; }
.field input:focus, .field select:focus { border-color: #667eea; outline: none; }
.field-row { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }
.dialog-footer { display: flex; justify-content: flex-end; gap: 10px; padding: 16px 20px; border-top: 1px solid #f0f0f0; }
.btn-cancel { padding: 10px 20px; background: #f5f5f5; border: none; border-radius: 8px; color: #666; font-size: 14px; cursor: pointer; }
.btn-confirm { padding: 10px 20px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); border: none; border-radius: 8px; color: #fff; font-size: 14px; cursor: pointer; }
.btn-confirm:disabled { opacity: 0.5; }

/* 数据表格样式 */
.data-table { width: 100%; border-collapse: collapse; }
.data-table th { text-align: left; padding: 10px; background: #f5f5f5; color: #666; font-size: 13px; }
.data-table td { padding: 10px; border-bottom: 1px solid #f0f0f0; font-size: 14px; }
.tag { padding: 3px 8px; border-radius: 4px; font-size: 12px; }
.tag.on { background: #e8f5e9; color: #2e7d32; }
.tag.off { background: #f5f5f5; color: #666; }
.empty-cell { text-align: center; color: #888; }
</style>
