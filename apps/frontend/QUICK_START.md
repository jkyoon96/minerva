# EduForum Seminar - Quick Start Guide

## Setup (First Time)

```bash
# 1. Install dependencies
cd apps/frontend
npm install

# 2. Create environment file
cat > .env.local << 'ENVEOF'
NEXT_PUBLIC_API_URL=http://localhost:8000/api
NEXT_PUBLIC_WS_URL=http://localhost:8000
ENVEOF

# 3. Start dev server
npm run dev
```

## Access Seminar

Open browser to: `http://localhost:3000/seminar/1`

Replace `1` with your actual room ID.

## Key Features to Test

### Video
- Camera on/off
- Grid view with multiple participants
- Speaking indicator (blue ring)

### Audio
- Mute/unmute
- Muted indicator

### Screen Share
- Share screen button
- Full-screen display
- PIP video overlay

### Chat
- Send public/private messages
- Search messages
- Delete own messages

### Interactions
- Raise hand
- Send emoji reactions

### Layout (Host Only)
- Gallery/Speaker/Sidebar views

## Quick API Reference

```typescript
// Join room
seminarApi.participant.joinRoom(roomId, { userName })

// Send chat
seminarApi.chat.sendMessage(roomId, { content })

// Toggle hand
seminarApi.participant.toggleHandRaise(roomId, participantId, isRaised)

// Change layout
seminarApi.room.updateLayout(roomId, { layout })
```

## Troubleshooting

- **Camera not working**: Check browser permissions
- **WebSocket disconnects**: Verify backend is running
- **No video**: Check video toggle is ON

## Browser Support

✅ Chrome 90+, Edge 90+ (Recommended)
⚠️ Firefox 88+, Safari 14+

---

For full documentation, see `SEMINAR_README.md`
