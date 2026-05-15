<template>
  <div class="floating-chars" :class="{ interactive: interactive }">
    <span
      v-for="(ch, i) in characters"
      :key="i"
      class="float-char"
      :style="ch.style"
    >{{ ch.emoji }}</span>
  </div>
</template>

<script setup>
const props = defineProps({
  count: { type: Number, default: 12 },
  interactive: { type: Boolean, default: false }
});

const emojis = ['🧑', '👧', '👦', '🧒', '🐱', '🌸', '⭐', '🎈', '🍭', '🌻', '🐰', '🎀', '🌈', '💫', '🦋', '🍀'];

const characters = Array.from({ length: props.count }, (_, i) => {
  const size = 20 + Math.random() * 24;
  const x = 5 + Math.random() * 90;
  const y = 5 + Math.random() * 90;
  const duration = 6 + Math.random() * 10;
  const delay = Math.random() * 8;
  const drift = (Math.random() - 0.5) * 60;
  const opacity = 0.2 + Math.random() * 0.25;
  return {
    emoji: emojis[i % emojis.length],
    style: {
      fontSize: `${size}px`,
      left: `${x}%`,
      top: `${y}%`,
      animationDuration: `${duration}s`,
      animationDelay: `${delay}s`,
      '--drift': `${drift}px`,
      opacity
    }
  };
});
</script>

<style scoped>
.floating-chars {
  position: absolute;
  inset: 0;
  pointer-events: none;
  overflow: hidden;
  z-index: 0;
}

.floating-chars.interactive {
  pointer-events: auto;
}

.float-char {
  position: absolute;
  animation: floatAround ease-in-out infinite;
  filter: drop-shadow(0 2px 4px rgba(0,0,0,0.06));
  user-select: none;
}

@keyframes floatAround {
  0%, 100% {
    transform: translateY(0) translateX(0) rotate(0deg);
  }
  25% {
    transform: translateY(-18px) translateX(calc(var(--drift) * 0.5)) rotate(5deg);
  }
  50% {
    transform: translateY(-8px) translateX(var(--drift)) rotate(-3deg);
  }
  75% {
    transform: translateY(-22px) translateX(calc(var(--drift) * 0.3)) rotate(4deg);
  }
}
</style>
