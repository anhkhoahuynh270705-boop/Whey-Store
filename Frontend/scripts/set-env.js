const fs = require('fs');
const path = require('path');

const envPath = path.resolve(__dirname, '../.env');
const targetPath = path.resolve(__dirname, '../src/environments/environment.ts');
const targetProdPath = path.resolve(__dirname, '../src/environments/environment.prod.ts');

// Hàm đọc và parse file .env đơn giản
function parseEnv(filePath) {
  const env = {};
  if (!fs.existsSync(filePath)) {
    console.warn(`[set-env] File .env không tồn tại tại ${filePath}. Dùng giá trị mặc định.`);
    return env;
  }
  const content = fs.readFileSync(filePath, 'utf-8');
  content.split('\n').forEach(line => {
    const trimmed = line.trim();
    if (trimmed && !trimmed.startsWith('#')) {
      const idx = trimmed.indexOf('=');
      if (idx !== -1) {
        const key = trimmed.substring(0, idx).trim();
        const value = trimmed.substring(idx + 1).trim();
        env[key] = value;
      }
    }
  });
  return env;
}

const env = parseEnv(envPath);

const apiUrl = env.API_URL || process.env.API_URL || 'http://localhost:8080/api';
const keycloakUrl = env.KEYCLOAK_URL || process.env.KEYCLOAK_URL || 'http://localhost:8090';
const keycloakRealm = env.KEYCLOAK_REALM || process.env.KEYCLOAK_REALM || 'gymstore';
const keycloakClientId = env.KEYCLOAK_CLIENT_ID || process.env.KEYCLOAK_CLIENT_ID || 'gymstore-frontend';
const keycloakClientSecret = env.KEYCLOAK_CLIENT_SECRET || process.env.KEYCLOAK_CLIENT_SECRET || '';

// Đảm bảo thư mục src/environments tồn tại
const envDir = path.dirname(targetPath);
if (!fs.existsSync(envDir)) {
  fs.mkdirSync(envDir, { recursive: true });
}

// 1. Tạo file environment.ts (Development)
const envConfigFile = `// File này được tự động tạo từ .env bởi scripts/set-env.js
export const environment = {
  production: false,
  apiUrl: '${apiUrl}',
  keycloakUrl: '${keycloakUrl}',
  keycloakRealm: '${keycloakRealm}',
  keycloakClientId: '${keycloakClientId}',
  keycloakClientSecret: '${keycloakClientSecret}',
};
`;

fs.writeFileSync(targetPath, envConfigFile, 'utf-8');
console.log(`[set-env] ✅ Đã cập nhật ${targetPath} từ .env`);

// 2. Tạo file environment.prod.ts (Production)
const prodApiUrl = apiUrl.startsWith('http://localhost') ? '/api' : apiUrl;
const envProdConfigFile = `// File này được tự động tạo từ .env bởi scripts/set-env.js
export const environment = {
  production: true,
  apiUrl: '${prodApiUrl}',
  keycloakUrl: '${keycloakUrl}',
  keycloakRealm: '${keycloakRealm}',
  keycloakClientId: '${keycloakClientId}',
  keycloakClientSecret: '${keycloakClientSecret}',
};
`;

fs.writeFileSync(targetProdPath, envProdConfigFile, 'utf-8');
console.log(`[set-env] ✅ Đã cập nhật ${targetProdPath}`);
