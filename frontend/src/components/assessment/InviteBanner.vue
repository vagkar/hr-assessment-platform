<script setup>
import { ref } from 'vue'
import BaseButton from '@/components/ui/BaseButton.vue'
import { copyToClipboard } from '@/utils/clipboard'

const props = defineProps({
  inviteToken: { type: String, required: true },
  autoCopied: { type: Boolean, default: false },
})

const emit = defineEmits(['dismiss'])

const appUrl = import.meta.env.VITE_APP_URL
const copied = ref(props.autoCopied)

if (props.autoCopied) {
  setTimeout(() => (copied.value = false), 2000)
}

async function copyLink() {
  const ok = await copyToClipboard(`${appUrl}/candidate/${props.inviteToken}`)
  if (ok) {
    copied.value = true
    setTimeout(() => (copied.value = false), 2000)
  }
}
</script>

<template>
  <div class="card fade-in invite-banner">
    <div class="invite-banner__body">
      <div class="invite-banner__check">✓</div>
      <div>
        <div class="invite-banner__label">Invite sent to candidate</div>
        <div class="mono invite-banner__url">{{ appUrl }}/candidate/{{ inviteToken }}</div>
      </div>
    </div>
    <div class="invite-banner__actions">
      <BaseButton variant="ghost" sm @click="copyLink">{{ copied ? '✓ Copied' : 'Copy link' }}</BaseButton>
      <BaseButton variant="link" sm @click="emit('dismiss')">✕</BaseButton>
    </div>
  </div>
</template>

<style scoped>
.invite-banner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 18px 22px;
  margin-bottom: 22px;
  background: color-mix(in srgb, var(--ok) 8%, var(--surface));
  border-color: color-mix(in srgb, var(--ok) 25%, var(--rule));
}
.invite-banner__body { display: flex; align-items: center; gap: 14px; min-width: 0; flex: 1; }
.invite-banner__body > div { min-width: 0; }
.invite-banner__check {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: var(--ok);
  color: #fff;
  display: grid;
  place-items: center;
  flex-shrink: 0;
}
.invite-banner__label { font-weight: 500; }
.invite-banner__url {
  font-size: 11px;
  color: var(--muted);
  margin-top: 2px;
  word-break: break-all;
}
.invite-banner__actions { display: flex; gap: 8px; flex-shrink: 0; }

@media (max-width: 480px) {
  .invite-banner { flex-direction: column; align-items: flex-start; }
}
</style>
