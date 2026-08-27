import { computed, watchEffect } from "vue";

import type { PlayOptions } from "./audio";
import { useToolbar } from "~/composables/main/dictation";
import { usePronunciation } from "~/composables/user/pronunciation";
import { useCourseStore } from "~/store/course";
import { play, playSequence, updateSource } from "./audio";

const { getPronunciationUrl } = usePronunciation();

const spokenAcronyms = new Map<string, string>([
  ["api", "API"],
  ["crud", "CRUD"],
  ["dto", "DTO"],
  ["http", "HTTP"],
  ["https", "HTTPS"],
  ["jpa", "JPA"],
  ["json", "JSON"],
  ["jwt", "JWT"],
  ["mvc", "MVC"],
  ["pom", "POM"],
  ["sql", "SQL"],
  ["url", "URL"],
  ["xml", "XML"],
  ["yaml", "YAML"],
]);

/** 把驼峰、首字母缩写和文件名拆成有道词典能识别的独立发音单元。 */
export function splitTechnicalTerm(term: string): string[] {
  const cleaned = term.trim().replace(/^[^a-zA-Z0-9]+|[^a-zA-Z0-9]+$/g, "");
  if (!cleaned) return [];

  // 带空格的是句子或已经人工分好的短语，不再破坏原有整句朗读。
  if (/\s/.test(cleaned)) return [cleaned];

  return cleaned
    .replace(/[_.\/-]+/g, " ")
    .replace(/([a-z\d])([A-Z])/g, "$1 $2")
    .replace(/([A-Z]+)([A-Z][a-z])/g, "$1 $2")
    .split(/\s+/)
    .filter(Boolean)
    .map((word) => spokenAcronyms.get(word.toLowerCase()) ?? word);
}

function pronunciationUrls(english: string | undefined): string[] {
  if (!english) return [];
  return splitTechnicalTerm(english).map(getPronunciationUrl);
}

function playPronunciationUrls(urls: string[], options?: PlayOptions) {
  if (urls.length === 0) return () => {};
  if (urls.length > 1) return playSequence(urls, options);
  if (urls[0]) updateSource(urls[0]);
  return play(options);
}

let lastPronunciationUrl = "";
export function useCurrentStatementEnglishSound() {
  const courseStore = useCourseStore();
  const { toolBarData } = useToolbar();

  const currentPronunciationUrls = computed(() =>
    pronunciationUrls(courseStore.currentStatement?.english),
  );

  watchEffect(() => {
    const pronunciationUrl = currentPronunciationUrls.value[0] ?? "";
    if (lastPronunciationUrl !== pronunciationUrl) {
      updateSource(pronunciationUrl);
    }
    lastPronunciationUrl = pronunciationUrl;
  });

  return {
    playSound: (options?: PlayOptions) => {
      const { times, rate, interval } = toolBarData;
      return playPronunciationUrls(currentPronunciationUrls.value, {
        times,
        rate,
        interval,
        ...options,
      });
    },
  };
}

// 朗读每日一句
export function readOneSentencePerDayAloud(str: string) {
  const pronunciationUrl = getPronunciationUrl(str);
  updateSource(pronunciationUrl);
  play();
}

export function playEnglish(english: string) {
  playPronunciationUrls(pronunciationUrls(english));
}
