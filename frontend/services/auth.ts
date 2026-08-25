// 无鉴权（访客模式）版本 —— 保留原项目 auth 接口签名，但始终未登录，
// 使 core 流程中 isAuthenticated() 相关分支（掌握/暂停/学习计时等）自然失效。
export async function setupAuth() {}

export async function signIn(callback?: string) {
  if (callback) {
    setSignInCallback(callback);
  }
}

export function signOut() {}

export function isAuthenticated() {
  return false;
}

export async function getToken() {
  return "";
}

export function fetchUserInfo() {
  return null;
}

export function getSignInCallback() {
  return "/";
}

function setSignInCallback(callback: string) {
  sessionStorage.setItem("callback", callback);
}