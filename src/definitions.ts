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
  deviceInfo(): Promise<{ serial_number: string, model: string }>
}
