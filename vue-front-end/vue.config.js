module.exports = {
  devServer: {
    proxy: {
      '/v1': {
        target: 'http://localhost:8085',
        ws: true,
        changeOrigin: true
      },
    }
  }
}