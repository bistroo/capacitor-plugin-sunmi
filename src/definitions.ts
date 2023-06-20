import type { PluginListenerHandle } from '@capacitor/core'

export type TableRow = {
  value: string
  size: number
  alignment: 0 | 1 | 2
}

export interface SunmiPlugin {
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
  fontSize(options: { size: 1 | 2 | 3 | 4 }): void
  deviceInfo(): Promise<{ serial_number: string, model: string }>
  raw(text: string): Promise<void>
  addListener(
    eventName: 'printer-state',
    listenerFunc: (response: { status: number }) => void,
  ): Promise<PluginListenerHandle> & PluginListenerHandle;
}
