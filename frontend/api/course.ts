import { type Course } from "~/types";
import { getHttp } from "./http";

export interface StatementApiResponse {
  id: string;
  order: number;
  chinese: string;
  english: string;
  soundmark: string;
  isMastered: boolean;
}

export interface CourseApiResponse {
  id: string;
  title: string;
  description: string;
  order: number;
  statements: StatementApiResponse[];
  coursePackId: string;
  completionCount: number;
  statementIndex: number;
  video: string;
  /* 新增：知识点/注解源码实现 + 使用场景说明（练习页侧栏展示） */
  annotationCode?: string;
  annotationExplain?: string;
  /* 新增：练习模式 WORD(单词) / SENTENCE(整句)；个人笔记 */
  practiceType?: "WORD" | "SENTENCE" | string;
  note?: string;
}

export async function fetchCourse(coursePackId: string, courseId: string) {
  const http = getHttp();
  return (await http<CourseApiResponse>(`course-pack/${coursePackId}/courses/${courseId}`, {
    method: "get",
  })) as Course;
}

type CompleteCourseResponse = { nextCourse: CourseApiResponse | undefined };
export async function fetchCompleteCourse(coursePackId: string, courseId: string) {
  const http = getHttp();
  return transformerFetchCompleteCourse(
    await http<CompleteCourseResponse>(
      `/course-pack/${coursePackId}/courses/${courseId}/complete`,
      { method: "post" },
    ),
  );
}

function transformerFetchCompleteCourse(apiResponse: CompleteCourseResponse): {
  nextCourse: Course | undefined;
} {
  return {
    nextCourse: apiResponse.nextCourse as Course,
  };
}

/** 保存个人笔记（见解/心得）到知识点节点 */
export async function updateCourseNote(coursePackId: string, courseId: string, note: string) {
  const http = getHttp();
  return (await http<CourseApiResponse>(`/course-pack/${coursePackId}/courses/${courseId}`, {
    method: "put",
    body: { note },
  })) as Course;
}
