export interface SunmiPlugin {
  printText(options: { value: string }): Promise<void>
}
