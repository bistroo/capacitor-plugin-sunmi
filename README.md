# @bistroo/capacitor-plugin-sunmi

## Install

```bash
pnpm add -D @bistroo/capacitor-plugin-sunmi
```

```typescript
import { Sunmi } from '@bistroo/capacitor-plugin-sunmi'

// start the buffer
Sunmi.start()
Sunmi.line('fdfdfdfd')

// commit the buffer
await Sunmi.print()
```

Or without using a buffer

```typescript
import { Sunmi } from '@bistroo/capacitor-plugin-sunmi'

Sunmi.line('fdfdfdfd')
```

## API
```typescript
interface SunmiPlugin {
  start(): void
  table(options: { rows: TableRow[] }): void
  text(options: { text: string }): void
  line(options: { text?: string, wrap: boolean }): void
  wrap(): void
  bold(): void
  normal(): void
  align(options: { direction: "LEFT" | "CENTER" | "RIGHT" }): void
  print(): Promise<void>
  image(options: { image: string }): void
  font(options: { type: number }): void
  fontSize(options: { size: 1 | 2 | 3 | 4 }): void
  deviceInfo(): Promise<{ serial_number: string, model: string }>
  addListener(
    eventName: 'printer-state',
    listenerFunc: (response: { status: number }) => void,
  ): Promise<PluginListenerHandle> & PluginListenerHandle;
}

export type TableRow = {
  value: string
  size: number
  alignment: 0 | 1 | 2
}

export const Sunmi: SunmiPlugin
```
