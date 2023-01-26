import { registerPlugin } from '@capacitor/core'
import type { SunmiPlugin } from './definitions'

const sunmiPlugin = registerPlugin<SunmiPlugin>('SunmiPlugin')

type TableRow = {
  value: string
  size: number
  alignment: 0 | 1 | 2
}

interface Builder {
  table(rows: TableRow[]): this
  text(value: string): this
  line(text?: string): this
  left(): this
  center(): this
  right(): this
  bold(bold?: boolean): this
  image(image: string): this
}

class CapacitorBuilder implements Builder {
  constructor() {
    sunmiPlugin.start()
  }

  table(rows: TableRow[]) {
    sunmiPlugin.table({ rows })

    return this
  }

  text(value: string) {
    sunmiPlugin.text({ text: value })

    return this
  }

  line(text?: string) {
    sunmiPlugin.line({ text: text ?? '' })

    return this
  }

  left() {
    sunmiPlugin.align({ direction: "LEFT" })

    return this
  }

  center() {
    sunmiPlugin.align({ direction: "CENTER" })

    return this
  }

  right() {
    sunmiPlugin.align({ direction: "RIGHT" })

    return this
  }

  bold(bold?: boolean) {
    bold ? sunmiPlugin.bold() : sunmiPlugin.normal()

    return this
  }

  image(image: string) {
    sunmiPlugin.image({ image })

    return this
  }
}

export { CapacitorBuilder as Builder }

export async function print() {
  await sunmiPlugin.print()
}
