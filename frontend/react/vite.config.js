import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
})


// // https://vitejs.dev/config/
// export default defineConfig({
//   plugins: [react()],
//   server: {
//     proxy: {
//       '/dev': {
//         target: 'https://j11e202.p.ssafy.io', // 실제 API 서버 주소
//         changeOrigin: true,  // cross-origin을 허용
//         rewrite: (path) => path.replace(/^\/dev/, '') // '/dev'로 시작하는 경로를 실제 API 경로로 변경
//       }
//     }
//   }
// })

