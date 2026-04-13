<template>
  <div class="footer">
    <div class="footer-content">
      <div class="footer-section">
        <h3>研路无忧</h3>
        <p>一站式考研服务平台，助你轻松上岸。</p>
      </div>
      <div class="footer-section">
        <h3>快速链接</h3>
        <el-button link @click="$router.push('/')">首页</el-button>
        <el-button link @click="$router.push('/materials')">资料商城</el-button>
        <el-button link @click="$router.push('/guides')">报考指南</el-button>
        <el-button link @click="$router.push('/forum')">交流论坛</el-button>
      </div>
      <div class="footer-section">
        <h3>联系我们</h3>
        <p>Email: contact@yanluwuyou.com</p>
        <p>Tel: 400-123-4567</p>
      </div>
    </div>
    <div class="copyright">
      &copy; 2026 研路无忧. All Rights Reserved.
    </div>
  </div>
</template>

<script setup>
</script>

<style scoped>
.footer {
    background-color: #2c3e50;
    color: #fff;
    padding: 40px 0 20px;
    margin-top: 60px;
}
.footer-content {
    max-width: 1200px;
    margin: 0 auto;
    display: flex;
    justify-content: space-around;
    flex-wrap: wrap;
    padding: 0 20px;
}
.footer-section {
    margin-bottom: 20px;
}
.footer-section h3 {
    margin-bottom: 15px;
    font-size: 18px;
    color: #ecf0f1;
}
.footer-section p {
    color: #bdc3c7;
    line-height: 1.6;
    margin-bottom: 5px;
}
.footer-section .el-button {
    color: #bdc3c7;
    display: block;
    margin-left: 0;
    margin-bottom: 5px;
}
.footer-section .el-button:hover {
    color: #409EFF;
}
.copyright {
    text-align: center;
    margin-top: 30px;
    padding-top: 20px;
    border-top: 1px solid #34495e;
    color: #95a5a6;
    font-size: 14px;
}
</style>
