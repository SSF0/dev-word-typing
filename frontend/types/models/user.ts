import type { SetupUserApiResponse } from "~/api/user";
import { type UserApiResponse } from "~/api/user";

export interface SetupUser extends SetupUserApiResponse {}

// 无鉴权（访客）版本 —— 移除 @logto 依赖，仅保留接口返回结构
export type User = UserApiResponse & {
  avatar: string;
  id: string;
};