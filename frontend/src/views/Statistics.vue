<template>
  <div class="stats-page">
    <div class="stats-grid">
      <div class="stat-card blue"><div class="stat-icon"><span class="mdi mdi-devices"></span></div><div class="stat-value">{{ totalDevicesCount }}</div><div class="stat-label">设备总数</div></div>
      <div class="stat-card green"><div class="stat-icon"><span class="mdi mdi-wifi"></span></div><div class="stat-value">{{ onlineDeviceCount }}</div><div class="stat-label">在线设备</div></div>
      <div class="stat-card orange"><div class="stat-icon"><span class="mdi mdi-alert"></span></div><div class="stat-value">{{ totalAlertsCount }}</div><div class="stat-label">警报总数</div></div>
      <div class="stat-card purple"><div class="stat-icon"><span class="mdi mdi-gesture-tap"></span></div><div class="stat-value">{{ totalCommandsCount }}</div><div class="stat-label">操作次数</div></div>
    </div>

    <div class="charts-row">
      <div class="chart-card large">
        <div class="chart-header"><h3><span class="mdi mdi-chart-line"></span> 每月警报趋势（最近 12 个月）</h3></div>
        <div class="chart-body"><div id="onlineRateChart" style="width: 100%; height: 260px;"></div></div>
      </div>
      <div class="chart-card">
        <div class="chart-header"><h3><span class="mdi mdi-chart-pie"></span> 设备告警分布</h3></div>
        <div class="chart-body"><div id="alertTypeChart" style="width: 100%; height: 260px;"></div></div>
      </div>
    </div>

    <div class="chart-card">
      <div class="chart-header"><h3><span class="mdi mdi-chart-bar"></span> 设备操作频率 Top 5（30 天内）</h3></div>
      <div class="chart-body"><div id="deviceLoadChart" style="width: 100%; height: 280px;"></div></div>
    </div>
  </div>
</template>

<script>
import { mapState } from 'vuex'

export default {
  data: function() {
    return {
      stats: {
        totalDevices: 0,
        onlineDevices: 0,
        totalAlerts: 0,
        totalCommands: 0,
        monthlyAlerts: [],
        commandFrequency: [],
        devicesByType: {}
      },
      onlineRateChart: null,
      alertTypeChart: null,
      deviceLoadChart: null
    }
  },
  computed: {
    ...mapState(['devices', 'alerts']),
    onlineDeviceCount: function() {
      return this.devices.filter(function(d) { return d.powerState === true }).length
    },
    totalDevicesCount: function() {
      return this.devices.length
    },
    totalAlertsCount: function() {
      return this.stats.totalAlerts || 0
    },
    totalCommandsCount: function() {
      return this.stats.totalCommands || 0
    }
  },
  mounted: function() { 
    this.loadData()
    this.$store.dispatch('fetchDevices')
    this.$store.dispatch('fetchAlerts')
  },
  watch: {
    devices: {
      handler: function() {
        if (this.onlineRateChart) {
          this.onlineRateChart.setOption(this.getOnlineRateOption())
        }
        if (this.deviceLoadChart) {
          this.deviceLoadChart.setOption(this.getDeviceLoadOption())
        }
      },
      deep: true
    },
    alerts: {
      handler: function() {
        if (this.alertTypeChart) {
          this.alertTypeChart.setOption(this.getAlertTypeOption())
        }
      },
      deep: true
    }
  },
  beforeDestroy: function() {
    if (this.onlineRateChart) this.onlineRateChart.dispose()
    if (this.alertTypeChart) this.alertTypeChart.dispose()
    if (this.deviceLoadChart) this.deviceLoadChart.dispose()
  },
  methods: {
    loadData: function() { 
      var self = this; 
      this.$axios.get('/api/statistics').then(function(r) { 
        if (r.data.success) { 
          self.stats = r.data.data; 
          self.renderCharts() 
        } 
      }) 
    },
    renderCharts: function() {
      var self = this;
      
      var echartsScript = document.createElement('script');
      echartsScript.src = 'https://cdn.jsdelivr.net/npm/echarts@5.4.3/dist/echarts.min.js';
      echartsScript.onload = function() {
        self.initCharts();
      };
      
      if (!window.echarts) {
        document.head.appendChild(echartsScript);
      } else {
        self.initCharts();
      }
    },
    initCharts: function() {
      var echarts = window.echarts;
      if (!echarts) return;
      
      this.onlineRateChart = echarts.init(document.getElementById('onlineRateChart'));
      var rateOption = this.getOnlineRateOption();
      this.onlineRateChart.setOption(rateOption);
      
      this.alertTypeChart = echarts.init(document.getElementById('alertTypeChart'));
      var alertOption = this.getAlertTypeOption();
      this.alertTypeChart.setOption(alertOption);
      
      this.deviceLoadChart = echarts.init(document.getElementById('deviceLoadChart'));
      var loadOption = this.getDeviceLoadOption();
      this.deviceLoadChart.setOption(loadOption);
      
      window.addEventListener('resize', function() {
        if (self.onlineRateChart) self.onlineRateChart.resize();
        if (self.alertTypeChart) self.alertTypeChart.resize();
        if (self.deviceLoadChart) self.deviceLoadChart.resize();
      });
    },
    getOnlineRateOption: function() {
      var months = [];
      var alertCounts = [];
      var self = this;
      
      // 使用后端返回的每月警报数据
      if (this.stats.monthlyAlerts && this.stats.monthlyAlerts.length > 0) {
        this.stats.monthlyAlerts.forEach(function(item) {
          months.push(item.year + '-' + String(item.month).padStart(2, '0'));
          alertCounts.push(item.count);
        });
      } else {
        // 如果没有数据，显示最近 12 个月
        for (var i = 11; i >= 0; i--) {
          var d = new Date();
          d.setMonth(d.getMonth() - i);
          months.push(d.getFullYear() + '-' + String(d.getMonth() + 1).padStart(2, '0'));
          alertCounts.push(0);
        }
      }
      
      return {
        tooltip: { trigger: 'axis', formatter: '{b}<br/>警报数：{c}次' },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: { type: 'category', data: months, axisLine: { lineStyle: { color: '#eee' } }, axisLabel: { color: '#666', rotate: 45 } },
        yAxis: { type: 'value', axisLine: { show: false }, splitLine: { lineStyle: { color: '#f0f0f0' } }, axisLabel: { color: '#666' } },
        series: [{
          name: '警报数',
          type: 'bar',
          data: alertCounts,
          itemStyle: { 
            color: new window.echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#667eea' },
              { offset: 1, color: 'rgba(102, 126, 234, 0.3)' }
            ]),
            borderRadius: [4, 4, 0, 0]
          },
          barWidth: '60%'
        }]
      };
    },
    getAlertTypeOption: function() {
      var deviceAlertMap = {};
      var self = this;
      
      // 使用后端返回的警报数据（从 Vuex 的 alerts 状态获取）
      this.alerts.forEach(function(alert) {
        var deviceName = alert.deviceName || '未知设备';
        if (!deviceAlertMap[deviceName]) {
          deviceAlertMap[deviceName] = 0;
        }
        deviceAlertMap[deviceName]++;
      });
      
      var data = [];
      var colors = [
        '#ff6b6b', '#4ecdc4', '#ffe66d', '#a55eea', '#95afc0',
        '#54a0ff', '#5f27cd', '#00d2d3', '#ff9ff3', '#feca57'
      ];
      var colorIndex = 0;
      
      for (var deviceName in deviceAlertMap) {
        data.push({
          value: deviceAlertMap[deviceName],
          name: deviceName,
          itemStyle: { color: colors[colorIndex % colors.length] }
        });
        colorIndex++;
      }
      
      if (data.length === 0) {
        data.push({ value: 0, name: '暂无数据', itemStyle: { color: '#ddd' } });
      }
      
      return {
        tooltip: { trigger: 'item', formatter: '{b}: {c} 次 ({d}%)' },
        legend: { orient: 'vertical', right: '5%', top: 'center', textStyle: { color: '#666', fontSize: 12 } },
        series: [{
          name: '设备告警分布',
          type: 'pie',
          radius: ['40%', '70%'],
          center: ['35%', '50%'],
          avoidLabelOverlap: false,
          itemStyle: { borderRadius: 8, borderColor: '#fff', borderWidth: 2 },
          label: { show: false },
          emphasis: { label: { show: true, fontSize: 14, fontWeight: 'bold' } },
          labelLine: { show: false },
          data: data
        }]
      };
    },
    getDeviceLoadOption: function() {
      var self = this;
      
      // 使用后端返回的命令频率数据（30 天内）
      var commandFreq = this.stats.commandFrequency || [];
      
      // 按命令次数排序，取前 5 名
      commandFreq.sort(function(a, b) { return b.commandCount - a.commandCount; });
      var top5 = commandFreq.slice(0, 5);
      
      var devices = top5.map(function(d) { return d.deviceName; });
      var counts = top5.map(function(d) { return d.commandCount; });
      var colors = [
        '#667eea', '#4ecdc4', '#ffe66d', '#ff6b6b', '#a55eea'
      ];
      
      if (devices.length === 0) {
        devices = ['暂无数据'];
        counts = [0];
        colors = ['#ddd'];
      }
      
      return {
        tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' }, formatter: '{b}<br/>执行次数：{c}次' },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: { type: 'category', data: devices, axisLine: { lineStyle: { color: '#eee' } }, axisLabel: { color: '#666', rotate: 30 } },
        yAxis: { type: 'value', axisLine: { show: false }, splitLine: { lineStyle: { color: '#f0f0f0' } }, axisLabel: { color: '#666' } },
        series: [{
          name: '命令执行次数',
          type: 'bar',
          data: counts.map(function(val, idx) {
            return {
              value: val,
              itemStyle: { color: colors[idx % colors.length] }
            };
          }),
          barWidth: '60%',
          itemStyle: { borderRadius: [4, 4, 0, 0] }
        }]
      };
    }
  }
}
</script>

<style scoped>
.stats-page { padding: 0 32px 32px; max-width: 1400px; margin: 0 auto; }
.stats-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 20px; margin-bottom: 24px; }
.stat-card { background: #fff; border-radius: 12px; padding: 24px; text-align: center; }
.stat-icon { width: 52px; height: 52px; border-radius: 12px; display: flex; align-items: center; justify-content: center; margin: 0 auto 14px; }
.stat-icon .mdi { font-size: 26px; color: #fff; }
.stat-card.blue .stat-icon { background: #4285f4; }
.stat-card.green .stat-icon { background: #34a853; }
.stat-card.orange .stat-icon { background: #fbbc04; }
.stat-card.purple .stat-icon { background: #9c27b0; }
.stat-value { font-size: 32px; font-weight: 700; color: #333; }
.stat-label { font-size: 14px; color: #888; margin-top: 4px; }

.charts-row { display: grid; grid-template-columns: 2fr 1fr; gap: 20px; margin-bottom: 20px; }
.chart-card { background: #fff; border-radius: 12px; overflow: hidden; }
.chart-card.large { }
.chart-header { padding: 18px 20px; border-bottom: 1px solid #f0f0f0; }
.chart-header h3 { font-size: 15px; color: #333; margin: 0; display: flex; align-items: center; gap: 8px; }
.chart-header h3 .mdi { color: #667eea; font-size: 20px; }
.chart-body { padding: 20px; min-height: 260px; }
.no-data { display: flex; align-items: center; justify-content: center; height: 180px; color: #999; }

@media (max-width: 1200px) { .stats-grid { grid-template-columns: repeat(2, 1fr); } .charts-row { grid-template-columns: 1fr; } }
@media (max-width: 768px) { .stats-grid { grid-template-columns: 1fr; } }
</style>
