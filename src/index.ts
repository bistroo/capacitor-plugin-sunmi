import { registerPlugin } from '@capacitor/core'
import type { SunmiPlugin, TableRow } from './definitions'

const sunmiPlugin = registerPlugin<SunmiPlugin>('SunmiPlugin')

export function start() {
  sunmiPlugin.start()
}

export function table(options: { rows: TableRow[] }) {
  sunmiPlugin.table(options)
}

export function text(options: { text: string }) {
  sunmiPlugin.text(options)
}

export function line(options: { text?: string, wrap: boolean }) {
  sunmiPlugin.line(options)
}

export function wrap() {
  sunmiPlugin.wrap()
}

export function bold() {
  sunmiPlugin.bold()
}

export function normal() {
  sunmiPlugin.normal()
}

export function align(options: { direction: "LEFT" | "CENTER" | "RIGHT" }) {
  sunmiPlugin.align(options)
}

export function image(options: { image: string }) {
  sunmiPlugin.image(options)
}

export async function deviceInfo() {
  return await sunmiPlugin.deviceInfo()
}

export async function print() {
  await sunmiPlugin.print()
}

export { TableRow }
