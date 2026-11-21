import {
  Component,
  ElementRef,
  ViewChild,
  AfterViewInit,
  OnDestroy,
  HostListener,
} from '@angular/core';

@Component({
  selector: 'app-whey-intro',
  standalone: true,
  imports: [],
  templateUrl: './whey-intro.component.html',
  styleUrl: './whey-intro.component.scss',
})
export class WheyIntroComponent implements AfterViewInit, OnDestroy {
  @ViewChild('canvas') canvasRef!: ElementRef<HTMLCanvasElement>;

  private raf = 0;
  private startTime = 0;
  private readonly DURATION = 5600; // ms per loop

  ngAfterViewInit(): void {
    this.resizeCanvas();
    this.startTime = performance.now();
    this.render();
  }

  ngOnDestroy(): void {
    cancelAnimationFrame(this.raf);
  }

  @HostListener('window:resize')
  onResize(): void {
    this.resizeCanvas();
  }

  private resizeCanvas(): void {
    const canvas = this.canvasRef?.nativeElement;
    if (!canvas) return;
    const rect = canvas.getBoundingClientRect();
    canvas.width = rect.width * devicePixelRatio;
    canvas.height = rect.height * devicePixelRatio;
    const ctx = canvas.getContext('2d');
    if (ctx) ctx.scale(devicePixelRatio, devicePixelRatio);
  }

  //  Main render loop — loops forever
  private render(): void {
    const canvas = this.canvasRef.nativeElement;
    const rect = canvas.getBoundingClientRect();
    const W = rect.width;
    const H = rect.height;

    if (W === 0 || H === 0) {
      this.raf = requestAnimationFrame(() => this.render());
      return;
    }

    const ctx = canvas.getContext('2d')!;
    const now = performance.now();
    // t cycles 0 → 1 → 0 → 1 ... seamlessly
    const raw = ((now - this.startTime) % this.DURATION) / this.DURATION;
    // ease: slow in, slow out each loop
    const t = raw;

    // clear with CSS pixel size (ctx already scaled by dpr)
    ctx.clearRect(0, 0, W, H);

    this.drawBackground(ctx, W, H, t);
    this.drawVignette(ctx, W, H);
    this.drawParticles(ctx, W, H, t, now);
    this.drawLightBeam(ctx, W, H, t);
    this.drawProducts(ctx, W, H, t);
    this.drawReflection(ctx, W, H, t);
    this.drawText(ctx, W, H, t);
    this.drawScanlines(ctx, W, H);

    this.raf = requestAnimationFrame(() => this.render());
  }

  // ─────────────────────────────────────────────
  //  Background
  // ─────────────────────────────────────────────
  private drawBackground(ctx: CanvasRenderingContext2D, W: number, H: number, t: number): void {
    const grad = ctx.createRadialGradient(W * 0.6, H * 0.42, 0, W * 0.6, H * 0.42, W * 0.85);
    grad.addColorStop(0, '#1e1e1e');
    grad.addColorStop(0.5, '#141414');
    grad.addColorStop(1, '#080808');
    ctx.fillStyle = grad;
    ctx.fillRect(0, 0, W, H);

    // pulsing orange glow behind product — breath cycle
    const breathe = 0.06 + 0.025 * Math.sin(t * Math.PI * 2);
    const glow = ctx.createRadialGradient(W * 0.76, H * 0.5, 0, W * 0.76, H * 0.5, W * 0.38);
    glow.addColorStop(0, `rgba(255,107,26,${breathe})`);
    glow.addColorStop(0.6, `rgba(255,107,26,${breathe * 0.4})`);
    glow.addColorStop(1, 'rgba(255,107,26,0)');
    ctx.fillStyle = glow;
    ctx.fillRect(0, 0, W, H);
  }

  // ─────────────────────────────────────────────
  //  Vignette
  // ─────────────────────────────────────────────
  private drawVignette(ctx: CanvasRenderingContext2D, W: number, H: number): void {
    // Dark gradient phủ nửa trái để text Angular đọc rõ
    const left = ctx.createLinearGradient(0, 0, W * 0.55, 0);
    left.addColorStop(0, 'rgba(0,0,0,0.80)');
    left.addColorStop(0.5, 'rgba(0,0,0,0.55)');
    left.addColorStop(1, 'rgba(0,0,0,0)');
    ctx.fillStyle = left;
    ctx.fillRect(0, 0, W, H);

    // Vignette viền xung quanh
    const v = ctx.createRadialGradient(W / 2, H / 2, H * 0.25, W / 2, H / 2, H * 0.95);
    v.addColorStop(0, 'rgba(0,0,0,0)');
    v.addColorStop(1, 'rgba(0,0,0,0.65)');
    ctx.fillStyle = v;
    ctx.fillRect(0, 0, W, H);
  }

  // ─────────────────────────────────────────────
  //  Floating particles
  // ─────────────────────────────────────────────
  private particles: {
    x: number;
    y: number;
    r: number;
    a: number;
    speed: number;
    phase: number;
  }[] = [];

  private drawParticles(
    ctx: CanvasRenderingContext2D,
    W: number,
    H: number,
    t: number,
    now: number,
  ): void {
    if (this.particles.length === 0) {
      this.particles = Array.from({ length: 48 }, () => ({
        x: Math.random() * W,
        y: Math.random() * H,
        r: 0.6 + Math.random() * 1.8,
        a: 0.15 + Math.random() * 0.5,
        speed: 0.15 + Math.random() * 0.25,
        phase: Math.random() * Math.PI * 2,
      }));
    }
    this.particles.forEach((p) => {
      const drift = now * 0.0004 * p.speed;
      const px = (p.x + drift * 16) % W;
      const py = (((p.y - drift * 20 * p.speed) % H) + H) % H;
      const pa = p.a * (0.5 + 0.5 * Math.sin(now * 0.001 + p.phase));
      ctx.beginPath();
      ctx.arc(px, py, p.r, 0, Math.PI * 2);
      ctx.fillStyle = `rgba(255,255,255,${pa})`;
      ctx.fill();
    });
  }

  // ─────────────────────────────────────────────
  //  Repeating light beam sweep
  // ─────────────────────────────────────────────
  private drawLightBeam(ctx: CanvasRenderingContext2D, W: number, H: number, t: number): void {
    // sweeps across once per loop in t=0.15..0.55
    const local = (t - 0.15) / 0.4;
    if (local < 0 || local > 1) return;
    const x = W * 0.25 + local * W * 0.8;
    const beam = ctx.createLinearGradient(x - 50, 0, x + 50, 0);
    beam.addColorStop(0, 'rgba(255,255,255,0)');
    beam.addColorStop(0.5, 'rgba(255,255,255,0.045)');
    beam.addColorStop(1, 'rgba(255,255,255,0)');
    ctx.fillStyle = beam;
    ctx.fillRect(0, 0, W, H);
  }

  // ─────────────────────────────────────────────
  //  Products — smooth float animation (loop)
  // ─────────────────────────────────────────────
  private drawProducts(ctx: CanvasRenderingContext2D, W: number, H: number, t: number): void {
    // fade in first 20% of loop, full opacity after, fade out last 8%
    let alpha = 1;
    if (t < 0.2) alpha = this.easeOut(t / 0.2);
    if (t > 0.92) alpha = 1 - (t - 0.92) / 0.08;

    // gentle float: bob up/down
    const floatY = Math.sin(t * Math.PI * 2) * H * 0.012;

    ctx.save();
    ctx.globalAlpha = alpha;
    ctx.translate(0, floatY);

    const cx = W * 0.76;
    const cy = H * 0.5;
    const jH = H * 0.6;
    const jW = jH * 0.84;
    this.drawJar(ctx, cx + W * 0.03, cy - jH * 0.52, jW, jH, W, H);

    const bH = H * 0.5;
    const bW = bH * 0.19;
    this.drawBar(ctx, cx - W * 0.175, cy - bH * 0.56, bW, bH, -14);

    const bH2 = bH * 0.88;
    const bW2 = bW * 0.88;
    this.drawBar(ctx, cx - W * 0.08, cy - bH2 * 0.5, bW2, bH2, -8);

    ctx.restore();
  }

  // ─────────────────────────────────────────────
  //  Jar
  // ─────────────────────────────────────────────
  private drawJar(
    ctx: CanvasRenderingContext2D,
    x: number,
    y: number,
    w: number,
    h: number,
    W: number,
    H: number,
  ): void {
    const r = w * 0.08;
    const lidH = h * 0.082;

    // drop shadow
    ctx.save();
    ctx.shadowColor = 'rgba(0,0,0,0.65)';
    ctx.shadowBlur = 28;
    ctx.shadowOffsetY = 12;

    // body
    const bg = ctx.createLinearGradient(x - w / 2, 0, x + w / 2, 0);
    bg.addColorStop(0, '#181818');
    bg.addColorStop(0.18, '#2d2d2d');
    bg.addColorStop(0.42, '#383838');
    bg.addColorStop(0.6, '#282828');
    bg.addColorStop(0.82, '#1b1b1b');
    bg.addColorStop(1, '#0f0f0f');
    ctx.fillStyle = bg;
    this.rRect(ctx, x - w / 2, y + lidH, w, h - lidH, r);
    ctx.fill();
    ctx.restore();

    // left highlight
    const hl = ctx.createLinearGradient(x - w / 2, 0, x - w / 2 + w * 0.14, 0);
    hl.addColorStop(0, 'rgba(255,255,255,0.10)');
    hl.addColorStop(1, 'rgba(255,255,255,0)');
    ctx.fillStyle = hl;
    this.rRect(ctx, x - w / 2, y + lidH, w * 0.14, h - lidH, r);
    ctx.fill();

    // lid
    const lid = ctx.createLinearGradient(x - w / 2, 0, x + w / 2, 0);
    lid.addColorStop(0, '#101010');
    lid.addColorStop(0.3, '#252525');
    lid.addColorStop(0.7, '#1f1f1f');
    lid.addColorStop(1, '#0a0a0a');
    ctx.fillStyle = lid;
    this.rRect(ctx, x - w / 2 - w * 0.03, y, w * 1.06, lidH * 1.5, r * 0.7);
    ctx.fill();

    // label area
    const lY = y + lidH + h * 0.13;
    const lH = h * 0.54;
    const lW = w * 0.78;
    ctx.save();
    ctx.beginPath();
    this.rRect(ctx, x - lW / 2, lY, lW, lH, 4);
    ctx.clip();

    const lbg = ctx.createLinearGradient(0, lY, 0, lY + lH);
    lbg.addColorStop(0, '#1c1c1c');
    lbg.addColorStop(1, '#111');
    ctx.fillStyle = lbg;
    ctx.fillRect(x - lW / 2, lY, lW, lH);

    ctx.textAlign = 'center';
    ctx.textBaseline = 'middle';

    ctx.fillStyle = '#ebebeb';
    ctx.font = `900 ${lW * 0.22}px 'Arial Black', Arial, sans-serif`;
    ctx.fillText('WHEY', x, lY + lH * 0.26);
    ctx.fillText('PROTEIN', x, lY + lH * 0.47);

    ctx.fillStyle = '#ff6b1a';
    ctx.font = `bold italic ${lW * 0.135}px Arial, sans-serif`;
    ctx.fillText('BE STRONG!', x, lY + lH * 0.66);

    ctx.fillStyle = 'rgba(200,200,200,0.40)';
    ctx.font = `${lW * 0.072}px Arial, sans-serif`;
    ctx.fillText('Premium Whey Isolate', x, lY + lH * 0.8);
    ctx.fillText('30g Protein per Serving', x, lY + lH * 0.91);

    ctx.restore();

    // label border
    ctx.strokeStyle = 'rgba(255,255,255,0.07)';
    ctx.lineWidth = 1;
    ctx.beginPath();
    this.rRect(ctx, x - lW / 2, lY, lW, lH, 4);
    ctx.stroke();
  }

  // ─────────────────────────────────────────────
  //  Protein bar
  // ─────────────────────────────────────────────
  private drawBar(
    ctx: CanvasRenderingContext2D,
    x: number,
    y: number,
    w: number,
    h: number,
    angle: number,
  ): void {
    ctx.save();
    ctx.translate(x + w / 2, y + h / 2);
    ctx.rotate((angle * Math.PI) / 180);

    ctx.save();
    ctx.shadowColor = 'rgba(0,0,0,0.5)';
    ctx.shadowBlur = 18;
    ctx.shadowOffsetX = 5;
    ctx.shadowOffsetY = 8;
    const bg = ctx.createLinearGradient(-w / 2, 0, w / 2, 0);
    bg.addColorStop(0, '#181818');
    bg.addColorStop(0.25, '#282828');
    bg.addColorStop(0.55, '#2e2e2e');
    bg.addColorStop(0.8, '#1e1e1e');
    bg.addColorStop(1, '#0f0f0f');
    ctx.fillStyle = bg;
    this.rRect(ctx, -w / 2, -h / 2, w, h, 4);
    ctx.fill();
    ctx.restore();

    // highlight
    const hl = ctx.createLinearGradient(-w / 2, 0, -w / 2 + w * 0.16, 0);
    hl.addColorStop(0, 'rgba(255,255,255,0.09)');
    hl.addColorStop(1, 'rgba(255,255,255,0)');
    ctx.fillStyle = hl;
    this.rRect(ctx, -w / 2, -h / 2, w * 0.16, h, 4);
    ctx.fill();

    // label text (vertical)
    ctx.save();
    ctx.beginPath();
    this.rRect(ctx, -w / 2 + w * 0.07, -h / 2 + h * 0.07, w * 0.86, h * 0.86, 3);
    ctx.clip();

    ctx.fillStyle = '#181818';
    ctx.fillRect(-w / 2, -h / 2, w, h);

    ctx.rotate(-Math.PI / 2);
    ctx.textAlign = 'center';
    ctx.textBaseline = 'middle';

    ctx.fillStyle = '#e2e2e2';
    ctx.font = `900 ${h * 0.062}px 'Arial Black', Arial, sans-serif`;
    ctx.fillText('WHEY PROTEIN', 0, -h * 0.08);

    ctx.fillStyle = '#ff6b1a';
    ctx.font = `bold italic ${h * 0.05}px Arial, sans-serif`;
    ctx.fillText('BE STRONG!', 0, h * 0.06);

    ctx.restore();

    ctx.strokeStyle = 'rgba(255,255,255,0.06)';
    ctx.lineWidth = 0.8;
    ctx.beginPath();
    this.rRect(ctx, -w / 2, -h / 2, w, h, 4);
    ctx.stroke();

    ctx.restore();
  }

  // ─────────────────────────────────────────────
  //  Reflection
  // ─────────────────────────────────────────────
  private drawReflection(ctx: CanvasRenderingContext2D, W: number, H: number, t: number): void {
    let alpha = 0.18;
    if (t < 0.2) alpha *= t / 0.2;
    if (t > 0.92) alpha *= 1 - (t - 0.92) / 0.08;

    const floatY = Math.sin(t * Math.PI * 2) * H * 0.012;
    const mirrorY = H * 0.74;

    ctx.save();
    ctx.globalAlpha = alpha;
    ctx.translate(0, mirrorY * 2 + floatY * 2);
    ctx.scale(1, -1);

    const jH = H * 0.6;
    const jW = jH * 0.84;
    const cx = W * 0.76;
    const cy = H * 0.5;
    this.drawJar(ctx, cx + W * 0.03, cy - jH * 0.52, jW, jH, W, H);

    // fade the reflection downward
    const fade = ctx.createLinearGradient(0, 0, 0, mirrorY);
    fade.addColorStop(0, 'rgba(0,0,0,0)');
    fade.addColorStop(0.4, 'rgba(0,0,0,0.80)');
    fade.addColorStop(1, 'rgba(0,0,0,1)');
    ctx.fillStyle = fade;
    ctx.fillRect(0, 0, W, mirrorY);
    ctx.restore();
  }

  // ─────────────────────────────────────────────
  //  Typography — tắt khi dùng làm background
  //  (Angular hero text đè lên trực tiếp)
  // ─────────────────────────────────────────────
  private drawText(_ctx: CanvasRenderingContext2D, _W: number, _H: number, _t: number): void {
    // no-op: text được render bởi Angular hero-content
  }

  // ─────────────────────────────────────────────
  //  Scanlines
  // ─────────────────────────────────────────────
  private drawScanlines(ctx: CanvasRenderingContext2D, W: number, H: number): void {
    ctx.save();
    ctx.globalAlpha = 0.022;
    for (let y = 0; y < H; y += 4) {
      ctx.fillStyle = '#000';
      ctx.fillRect(0, y, W, 2);
    }
    ctx.restore();
  }

  // ─────────────────────────────────────────────
  //  Helpers
  // ─────────────────────────────────────────────
  private rRect(
    ctx: CanvasRenderingContext2D,
    x: number,
    y: number,
    w: number,
    h: number,
    r: number,
  ): void {
    r = Math.min(r, w / 2, h / 2);
    ctx.beginPath();
    ctx.moveTo(x + r, y);
    ctx.lineTo(x + w - r, y);
    ctx.quadraticCurveTo(x + w, y, x + w, y + r);
    ctx.lineTo(x + w, y + h - r);
    ctx.quadraticCurveTo(x + w, y + h, x + w - r, y + h);
    ctx.lineTo(x + r, y + h);
    ctx.quadraticCurveTo(x, y + h, x, y + h - r);
    ctx.lineTo(x, y + r);
    ctx.quadraticCurveTo(x, y, x + r, y);
    ctx.closePath();
  }

  private easeOut(t: number): number {
    return 1 - Math.pow(1 - t, 3);
  }

  private easeOutBack(t: number): number {
    const c1 = 1.70158,
      c3 = c1 + 1;
    return 1 + c3 * Math.pow(t - 1, 3) + c1 * Math.pow(t - 1, 2);
  }
}
