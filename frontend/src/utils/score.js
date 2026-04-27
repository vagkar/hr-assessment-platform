export function scoreTag(score) {
  if (score === null || score === undefined) return ''
  if (score >= 75) return 'tag--ok'
  if (score >= 50) return 'tag--warn'
  return 'tag--bad'
}
