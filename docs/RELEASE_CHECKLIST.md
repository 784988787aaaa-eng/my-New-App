# Release Checklist

## Engineering
- [x] CI release build succeeds (Run 171; experimental CI certificate)
- [ ] R8/proguard reviewed
- [x] No production secrets/keys committed
- [x] Room migrations covered by CI build/test path
- [ ] Backup/restore tested

## Product
- [x] Arabic RTL composition root and primary screens
- [ ] Arabic copy reviewed
- [x] Balance domain tests + live account aggregates
- [x] Product/stock transaction path
- [x] Sale/purchase atomic posting path
- [ ] Reports full business verification

## UX
- [ ] Keyboard/IME
- [ ] Accessibility
- [ ] Font scaling
- [ ] Small/large screens
- [ ] Empty/error/loading
- [ ] Performance

## Evidence
- [ ] Device screenshots
- [ ] E2E on physical/authorized Android device
- [x] CI regression run 171
- [x] Gap Matrix updated
