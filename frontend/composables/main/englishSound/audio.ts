import { usePronunciation } from "~/composables/user/pronunciation";

// 便于测试
// 后面不使用 audio 后也可以不破坏业务逻辑
const audio = new Audio();
export function updateSource(src: string) {
  audio.src = src;
  audio.load();
}

const { getPronunciationUrl } = usePronunciation();
export function usePlayWordSound() {
  const wordAudio = new Audio();
  let lastWord = "";
  let isPlaying = false;

  wordAudio.onplay = () => {
    isPlaying = true;
  };

  wordAudio.onended = () => {
    isPlaying = false;
  };

  function handlePlayWordSound(word: string) {
    if (isPlaying && lastWord === word) {
      // skip
      return;
    }
    lastWord = word;
    wordAudio.src = getPronunciationUrl(word);
    wordAudio.play();
  }

  return {
    handlePlayWordSound,
  };
}

export interface PlayOptions {
  times?: number;
  rate?: number;
  interval?: number;
}

const DefaultPlayOptions = {
  times: 1,
  rate: 1,
  interval: 500,
};

export function play(playOptions?: PlayOptions) {
  const { times, rate, interval } = Object.assign({}, DefaultPlayOptions, playOptions);

  audio.playbackRate = rate;
  audio.play();
  if (times > 1) {
    audio.addEventListener("ended", handleEnded, false);
  }

  let index = 1;
  let timeoutId: NodeJS.Timeout;
  function handleEnded() {
    timeoutId = setTimeout(() => {
      if (index < times) {
        audio.play();
        index++;
      } else {
        index = 1;
        audio.removeEventListener("ended", handleEnded);
      }
    }, interval);
  }

  return () => {
    audio.pause();
    audio.currentTime = 0;
    audio.removeEventListener("ended", handleEnded);
    timeoutId && clearTimeout(timeoutId);
  };
}

const TechnicalWordInterval = 180;

/**
 * 依次播放由同一个技术术语拆出的多个单词。
 * `interval` 仍用于整组重读，组内只保留较短停顿，避免听起来像互不相关的单词。
 */
export function playSequence(sources: string[], playOptions?: PlayOptions) {
  const playableSources = sources.filter(Boolean);
  if (playableSources.length === 0) return () => {};
  if (playableSources.length === 1) {
    updateSource(playableSources[0]);
    return play(playOptions);
  }

  const { times, rate, interval } = Object.assign({}, DefaultPlayOptions, playOptions);
  let sourceIndex = 0;
  let sequenceIndex = 1;
  let timeoutId: ReturnType<typeof setTimeout> | undefined;

  const startCurrentSource = () => {
    updateSource(playableSources[sourceIndex]);
    audio.playbackRate = rate;
    audio.play();
  };

  const cleanup = () => {
    audio.removeEventListener("ended", handleEnded);
    if (timeoutId) clearTimeout(timeoutId);
  };

  const scheduleCurrentSource = (delay: number) => {
    timeoutId = setTimeout(startCurrentSource, delay);
  };

  function handleEnded() {
    if (sourceIndex < playableSources.length - 1) {
      sourceIndex++;
      scheduleCurrentSource(TechnicalWordInterval);
      return;
    }

    if (sequenceIndex < times) {
      sequenceIndex++;
      sourceIndex = 0;
      scheduleCurrentSource(interval);
      return;
    }

    cleanup();
  }

  audio.addEventListener("ended", handleEnded, false);
  startCurrentSource();

  return () => {
    cleanup();
    audio.pause();
    audio.currentTime = 0;
  };
}
