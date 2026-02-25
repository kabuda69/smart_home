<template>
  <div class="stats-page">
    <div class="stats-grid">
      <div class="stat-card blue"><div class="stat-icon"><span class="mdi mdi-devices"></span></div><div class="stat-value">{{ stats.totalDevices || 0 }}</div><div class="stat-label">设备总数</div></div>
      <div class="stat-card green"><div class="stat-icon"><span class="mdi mdi-wifi"></span></div><div class="stat-value">{{ stats.onlineDevices || 0 }}</div><div class="stat-label">在线设备</div></div>
      <div class="stat-card orange"><div class="stat-icon"><span class="mdi mdi-alert"></span></div><div class="stat-value">{{ stats.totalAlerts || 0 }}</div><div class="stat-label">警报总数</div></div>
      <div class="stat-card purple"><div class="stat-icon"><span class="mdi mdi-gesture-tap"></span></div><div class="stat-value">{{ stats.totalCommands || 0 }}</div><div class="stat-label">操作次数</div></div>
    </div>

    <div class="charts-row">
      <div class="chart-card large">
        <div class="chart-header"><h3><span class="mdi mdi-chart-line"></span> 月度警报趋势</h3></div>
        <div class="chart-body"><line-chart v-if="alertChartData" :chart-data="alertChartData" :options="lineOptions" :height="260"></line-chart><div v-else class="no-data">暂无数据</div></div>
      </div>
      <div class="chart-card">
        <div class="chart-header"><h3><span class="mdi mdi-chart-pie"></span> 设备状态</h3></div>
        <div class="chart-body"><doughnut-chart v-if="deviceChartData" :chart-data="deviceChartData" :options="doughnutOptions" :height="260"></doughnut-chart><div v-else class="no-data">暂无数据</div></div>
      </div>
    </div>

    <div class="chart-card">
      <div class="chart-header"><h3><span class="mdi mdi-chart-bar"></span> 设备操作频率 (近30天)</h3></div>
      <div class="chart-body"><bar-chart v-if="commandChartData" :chart-data="commandChartData" :options="barOptions" :height="180"></bar-chart><div v-else class="no-data">暂无数据</div></div>
    </div>
  </div>
</template>

<script>
import { Line, Doughnut, Bar } from 'vue-chartjs'
var LineChart = { extends: Line, props: ['chartData', 'options'], mounted: function() { this.renderChart(this.chartData, this.options) }, watch: { chartData: function() { this.renderChart(this.chartData, this.options) } } }
var DoughnutChart = { extends: Doughnut, props: ['chartData', 'options'], mounted: function() { this.renderChart(this.chartData, this.options) }, watch: { chartData: function() { this.renderChart(this.chartData, this.options) } } }
var BarChart = { extends: Bar, props: ['chartData', 'options'], mounted: function() { this.renderChart(this.chartData, this.options) }, watch: { chartData: function() { this.renderChart(this.chartData, this.options) } } }

export default {
  components: { 'line-chart': LineChart, 'doughnut-chart': DoughnutChart, 'bar-chart': BarChart },
  data: function() {
    return {
      stats: {}, alertChartData: null, deviceChartData: null, commandChartData: null,
      lineOptions: { responsive: true, maintainAspectRatio: false, legend: { display: false }, scales: { yAxes: [{ ticks: { beginAtZero: true }, gridLines: { color: 'rgba(0,0,0,0.03)' } }], xAxes: [{ gridLines: { display: false } }] } },
      doughnutOptions: { responsive: true, maintainAspectRatio: false, legend: { position: 'bottom', labels: { padding: 16 } }, cutoutPercentage: 70 },
      barOptions: { responsive: true, maintainAspectRatio: false, legend: { display: false }, scales: { yAxes: [{ ticks: { beginAtZero: true }, gridLines: { color: 'rgba(0,0,0,0.03)' } }], xAxes: [{ gridLines: { display: false } }] } }
    }
  },
  mounted: function() { this.loadData() },
  methods: {
    loadData: function() { var self = this; this.$axios.get('/api/statistics').then(function(r) { if (r.data.success) { self.stats = r.data.data; self.renderCharts() } }) },
    renderCharts: function() {
      var ma = this.stats.monthlyAlerts || []; var labels = ma.map(function(m) { return m.month + '月' }); var data = ma.map(function(m) { return m.count })
      if (labels.length === 0) { labels = ['1月', '2月', '3月', '4月', '5月', '6月']; data = [0, 0, 0, 0, 0, 0] }
      this.alertChartData = { labels: labels, datasets: [{ data: data, borderColor: '#ff9800', backgroundColor: 'rgba(255, 152, 0, 0.1)', fill: true, lineTension: 0.4, pointBackgroundColor: '#ff9800', pointBorderColor: '#fff', pointBorderWidth: 2, pointRadius: 4 }] }
      var online = this.stats.onlineDevices || 0; var offline = (this.stats.totalDevices || 0) - online
      this.deviceChartData = { labels: ['在线', '离线'], datasets: [{ data: [online, offline], backgroundColor: ['#4caf50', '#e0e0e0'], borderWidth: 0 }] }
      var cf = this.stats.commandFrequency || []; var cLabels = cf.map(function(c) { return c.deviceName }); var cData = cf.map(function(c) { return c.commandCount })
      if (cLabels.length === 0) { cLabels = ['暂无']; cData = [0] }
      this.commandChartData = { labels: cLabels, datasets: [{ data: cData, backgroundColor: 'rgba(102, 126, 234, 0.8)', borderRadius: 6, maxBarThickness: 40 }] }
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
