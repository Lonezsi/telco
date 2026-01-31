import { useEffect, useRef } from "react";

export const useOverflowTooltip = <T extends HTMLElement>() => {
  const ref = useRef<T>(null);

  useEffect(() => {
    const el = ref.current;
    if (!el) return;

    let bubble: HTMLDivElement | null = null;

    const createBubble = (text: string) => {
      if (bubble) return bubble;
      bubble = document.createElement('div');
      bubble.className = 'overflow-tooltip-bubble';
      bubble.textContent = text;
      bubble.style.position = 'fixed';
      bubble.style.left = '0px';
      bubble.style.top = '0px';
      bubble.style.pointerEvents = 'auto';
      bubble.style.opacity = '0';
      bubble.style.transform = 'translateY(6px)';
      document.body.appendChild(bubble);
      return bubble;
    };

    const removeBubble = () => {
      if (!bubble) return;
      bubble.remove();
      bubble = null;
    };

    const show = () => {
      if (!el) return;
      const text = el.textContent || '';
      if (!text) return;
      const b = createBubble(text);
      // position above element, centered
      const rect = el.getBoundingClientRect();
      // allow bubble to size, then position centered
      requestAnimationFrame(() => {
        if (!b) return;
        const bw = b.offsetWidth;
        const bh = b.offsetHeight;
        let left = rect.left + rect.width / 2 - bw / 2;
        // clamp inside viewport
        left = Math.max(8, Math.min(left, window.innerWidth - bw - 8));
        let top = rect.top - bh + 20;
        // if not enough space above, place below
        if (top < 8) top = rect.bottom - 100;
        b.style.left = `${left}px`;
        b.style.top = `${top}px`;
        b.style.opacity = '1';
        b.style.transform = 'translateY(-6px)';
      });
    };

    const hide = () => {
      if (!bubble) return;
      bubble.style.opacity = '0';
      bubble.style.transform = 'translateY(6px)';
      // remove after transition
      setTimeout(removeBubble, 200);
    };

    const checkOverflow = () => {
      if (el.scrollWidth > el.clientWidth) {
        el.setAttribute('data-overflow', 'true');
        el.addEventListener('mouseenter', show);
        el.addEventListener('mouseleave', hide);
      } else {
        el.removeAttribute('data-overflow');
        el.removeEventListener('mouseenter', show);
        el.removeEventListener('mouseleave', hide);
      }
    };

    checkOverflow();

    const resizeObserver = new ResizeObserver(checkOverflow);
    resizeObserver.observe(el);

    return () => {
      resizeObserver.disconnect();
      el.removeEventListener('mouseenter', show);
      el.removeEventListener('mouseleave', hide);
      removeBubble();
    };
  }, []);

  return ref;
};
