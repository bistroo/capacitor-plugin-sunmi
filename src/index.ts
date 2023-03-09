import { registerPlugin } from '@capacitor/core'
import type { SunmiPlugin, TableRow } from './definitions'

const Sunmi = registerPlugin<SunmiPlugin>('SunmiPlugin')

export { Sunmi, TableRow }
