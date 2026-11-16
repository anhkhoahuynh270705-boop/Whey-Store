import {
  ChangeDetectorRef,
  Component,
  ElementRef,
  inject,
  OnDestroy,
  OnInit,
  ViewChild,
} from '@angular/core';
import * as THREE from 'three';

@Component({
  selector: 'app-three-bg',
  standalone: true,
  template: `<canvas #canvas class="three-canvas"></canvas>`,
  styles: [`
    :host {
      display: block;
      position: absolute;
      inset: 0;
      pointer-events: none;
      overflow: hidden;
    }
    .three-canvas {
      width: 100%;
      height: 100%;
      display: block;
    }
  `],
})
export class ThreeBgComponent implements OnInit, OnDestroy {
  @ViewChild('canvas', { static: true }) canvasRef!: ElementRef<HTMLCanvasElement>;
  protected cdr = inject(ChangeDetectorRef);

  private renderer!: THREE.WebGLRenderer;
  private scene!: THREE.Scene;
  private camera!: THREE.PerspectiveCamera;
  private particles!: THREE.Points;
  private animId!: number;
  private ro!: ResizeObserver;

  ngOnInit(): void {
    this.init();
    this.animate();
    this.listenResize();
  }

  ngOnDestroy(): void {
    cancelAnimationFrame(this.animId);
    this.ro?.disconnect();
    this.renderer.dispose();
  }

  private init(): void {
    const canvas = this.canvasRef.nativeElement;
    const w = canvas.clientWidth || 800;
    const h = canvas.clientHeight || 500;

    /* Renderer */
    this.renderer = new THREE.WebGLRenderer({
      canvas,
      alpha: true,
      antialias: false,
    });
    this.renderer.setPixelRatio(Math.min(devicePixelRatio, 2));
    this.renderer.setSize(w, h, false);
    this.renderer.setClearColor(0x000000, 0);

    /* Scene & Camera */
    this.scene = new THREE.Scene();
    this.camera = new THREE.PerspectiveCamera(55, w / h, 0.1, 1000);
    this.camera.position.z = 55;

    /* Particles — floating dots */
    const count = 1600;
    const positions = new Float32Array(count * 3);
    const colors = new Float32Array(count * 3);
    const sizes = new Float32Array(count);

    const primary = new THREE.Color('#ff6b1a');
    const accent  = new THREE.Color('#38bdf8');
    const dim     = new THREE.Color('#253548');

    for (let i = 0; i < count; i++) {
      const i3 = i * 3;
      positions[i3]     = (Math.random() - 0.5) * 180;
      positions[i3 + 1] = (Math.random() - 0.5) * 100;
      positions[i3 + 2] = (Math.random() - 0.5) * 60;

      const r = Math.random();
      const c = r < 0.1 ? primary : r < 0.18 ? accent : dim;
      colors[i3]     = c.r;
      colors[i3 + 1] = c.g;
      colors[i3 + 2] = c.b;

      sizes[i] = r < 0.1 ? 2.4 : r < 0.18 ? 1.8 : 1.0 + Math.random() * 0.6;
    }

    const geo = new THREE.BufferGeometry();
    geo.setAttribute('position', new THREE.BufferAttribute(positions, 3));
    geo.setAttribute('color', new THREE.BufferAttribute(colors, 3));
    geo.setAttribute('size', new THREE.BufferAttribute(sizes, 1));

    const mat = new THREE.PointsMaterial({
      size: 1.4,
      vertexColors: true,
      transparent: true,
      opacity: 0.75,
      sizeAttenuation: true,
      depthWrite: false,
    });

    this.particles = new THREE.Points(geo, mat);
    this.scene.add(this.particles);

    /* Subtle wire-frame box grid */
    const gridHelper = new THREE.GridHelper(200, 30, 0x1e2d40, 0x0e1a28);
    gridHelper.position.y = -20;
    gridHelper.rotation.x = Math.PI / 8;
    this.scene.add(gridHelper);
  }

  private animate(): void {
    this.animId = requestAnimationFrame(() => this.animate());
    const t = performance.now() * 0.0003;

    this.particles.rotation.y = t * 0.08;
    this.particles.rotation.x = Math.sin(t * 0.4) * 0.04;

    // Drift camera gently
    this.camera.position.x = Math.sin(t * 0.3) * 4;
    this.camera.position.y = Math.cos(t * 0.2) * 2;
    this.camera.lookAt(0, 0, 0);

    this.renderer.render(this.scene, this.camera);
  }

  private listenResize(): void {
    const canvas = this.canvasRef.nativeElement;
    this.ro = new ResizeObserver(() => {
      const w = canvas.clientWidth;
      const h = canvas.clientHeight;
      this.camera.aspect = w / h;
      this.camera.updateProjectionMatrix();
      this.renderer.setSize(w, h, false);
    });
    this.ro.observe(canvas.parentElement!);
  }
}

