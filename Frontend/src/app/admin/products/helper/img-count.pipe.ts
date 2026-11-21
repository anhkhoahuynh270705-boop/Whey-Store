import { Pipe, PipeTransform } from '@angular/core';

/** Đếm số slot không null trong mảng imageSlots */
@Pipe({ name: 'imgCount', standalone: true })
export class ImgCountPipe implements PipeTransform {
  transform(slots: (unknown | null)[]): number {
    return slots.filter((s) => s !== null).length;
  }
}
