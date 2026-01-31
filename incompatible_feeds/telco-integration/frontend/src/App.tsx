import React, { useState, useEffect, useMemo, useRef, useLayoutEffect } from 'react';
import axios from 'axios';
import {
  Search,
  AlertTriangle,
  CheckCircle2,
  Package,
  Filter,
  Moon,
  Sun
} from 'lucide-react';
import type { Product } from './types';
import { EllipsisTd } from './components/EllipsisTd';
import './App.css';

/* _________________ TYPES _________________ */

type StatProps = {
  icon: React.ReactNode;
  label: string;
  value: number | string;
  danger?: boolean;
};

type SkeletonProps = {
  circle?: boolean;
  wide?: boolean;
  small?: boolean;
  tag?: boolean;
};

type TooltipProps = {
  text?: string;
  children: React.ReactNode;
};

/* _________________ SMALL COMPONENTS _________________ */

const Stat: React.FC<StatProps> = ({ icon, label, value, danger }) => (
  <div className={`stat ${danger ? 'danger' : ''}`}>
    {icon}
    <div>
      <span>{label}</span>
      <strong>{value}</strong>
    </div>
  </div>
);

const Skeleton: React.FC<SkeletonProps> = ({ circle, wide, small, tag }) => (
  <div className={`sk ${circle ? 'c' : ''} ${wide ? 'w' : ''} ${small ? 's' : ''} ${tag ? 't' : ''}`} />
);

const Tooltip: React.FC<TooltipProps> = ({ text, children }) => (
  <div className="tooltip">
    {children}
    {text && <span>{text}</span>}
  </div>
);

/* _________________ MAIN APP _________________ */

const App: React.FC = () => {

  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(true);

  const [filter, setFilter] = useState('');
  const [sortKey, setSortKey] = useState<'status'|'sku'|'name'|'manufacturer'|'finalPriceHuf'|'stock'|'source'>('sku');
  const [sortDir, setSortDir] = useState<'asc'|'desc'>('asc');
  const [onlyValid, setOnlyValid] = useState(false);
  const [dark, setDark] = useState<boolean>(() => {
    try {
      const v = localStorage.getItem('theme-dark');
      if (v !== null) return v === '1';
      return window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches;
    } catch {
      return false;
    }
  });

  useEffect(() => {
    try {
      const root = document.documentElement;
      if (dark) root.classList.add('theme-dark'); else root.classList.remove('theme-dark');
      localStorage.setItem('theme-dark', dark ? '1' : '0');
    } catch (e) {
      // ignore
    }
  }, [dark]);

  // fetch once filter in-memory
  useEffect(() => {
    const fetchData = async () => {
      setLoading(true);
      try {
        // if Docker use backend port on localhost
        const url = import.meta.env.PROD ? 'http://localhost:8080/products' : '/products';
        const res = await axios.get<Product[]>(url);
        
        setProducts(Array.isArray(res.data) ? res.data : []);
      } catch (e) {
        console.error(e);
        setProducts([]);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  const changeSortAny = (key: 'status'|'sku'|'name'|'manufacturer'|'finalPriceHuf'|'stock'|'source') => {
    if (sortKey === key) {
      setSortDir(prev => prev === 'asc' ? 'desc' : 'asc');
    } else {
      setSortKey(key);
      setSortDir('asc');
    }
  };

  const sortedProducts = useMemo(() => {
    // filter in-memory

    if (!Array.isArray(products)) {
      console.error("Products is not an array! Value:", products);
      return [];
    }

    const q = filter.trim().toLowerCase();
    let arr = products.filter(p => {
      if (onlyValid && !p.valid) return false;
      if (!q) return true;
      return (p.sku || '').toString().toLowerCase().includes(q)
        || (p.name || '').toLowerCase().includes(q)
        || (p.manufacturer || '').toLowerCase().includes(q)
        || (p.source || '').toLowerCase().includes(q);
    });

    const getVal = (p: Product, k: typeof sortKey) => {
      switch (k) {
        case 'status': return p.valid ? 1 : 0;
        case 'sku': return p.sku ?? '';
        case 'name': return p.name ?? '';
        case 'manufacturer': return p.manufacturer ?? '';
        case 'finalPriceHuf': return p.finalPriceHuf ?? 0;
        case 'stock': return p.stock ?? 0;
        case 'source': return p.source ?? '';
      }
    };

    arr = arr.sort((a, b) => {
      const va = getVal(a, sortKey as any);
      const vb = getVal(b, sortKey as any);

      if (typeof va === 'number' && typeof vb === 'number') {
        return sortDir === 'asc' ? va - vb : vb - va;
      }

      const sa = String(va).toLowerCase();
      const sb = String(vb).toLowerCase();
      if (sa === sb) return 0;
      return sortDir === 'asc' ? (sa < sb ? -1 : 1) : (sa < sb ? 1 : -1);
    });

    return arr;
  }, [products, filter, onlyValid, sortKey, sortDir]);

  const tableInnerRef = useRef<HTMLDivElement | null>(null);

  useLayoutEffect(() => {
    const el = tableInnerRef.current;
    if (!el) return;
    const prev = el.offsetHeight;
    el.style.height = prev + 'px';
    let raf = 0;
    let tid: any = null;
    raf = requestAnimationFrame(() => {
      const to = el.scrollHeight;
      el.style.transition = 'height 220ms ease';
      el.style.height = to + 'px';
      tid = setTimeout(() => {
        el.style.height = '';
        el.style.transition = '';
      }, 260);
    });

    return () => {
      cancelAnimationFrame(raf);
      if (tid) clearTimeout(tid);
    };
  }, [sortedProducts.length, loading]);

  const invalid = Array.isArray(products) ? products.filter(p => !p.valid) : [];
  const sources = Array.isArray(products) ? new Set(products.map(p => p.source)).size : 0;

  const skeletonRows = Array.from({ length: 8 });

  return (
    <div className="app-root">

      {/* HEADER */}
      <header className="header">

        <div className="brand">
          <img src="/icon.svg" alt="Telco Integration" width="64" height="64" />
          <div>
            <h1>Telco Integration</h1>
            <p>Unified Product View</p>
          </div>
        </div>

        <div className="toolbar">

          <div className="search">
            <Search size={18} />
            <input
              type="text"
              id='search'
              placeholder="Search SKU or name…"
              value={filter}
              onChange={e => setFilter(e.target.value)}
            />
          </div>

          <div className="valid-control">
            <label className="checkbox-wrap">
              <input
                id="valid"
                type="checkbox"
                checked={onlyValid}
                onChange={e => setOnlyValid(e.target.checked)}
              />
              <div className="valid-label">Valid only</div>
            </label>
          </div>

          <div className="theme-control">
            <button
              className={`theme-pill ${dark ? 'on' : ''}`}
              aria-pressed={dark}
              aria-label={dark ? 'Switch to light theme' : 'Switch to dark theme'}
              onClick={() => setDark(d => !d)}
              title={dark ? 'Light mode' : 'Dark mode'}
            >
              <span className="pill-track">
                <span className="pill-handle">
                  {dark ? <Moon size={12} /> : <Sun size={12} />}
                </span>
              </span>
            </button>
            <div className="theme-label">
              {dark ? <><Moon size={12} /> Dark Mode</> : <><Sun size={12} /> Light Mode</>}
            </div>
          </div>
        </div>
      </header>

      {/* STATS */}
      <section className="stats">

        <Stat icon={<Package size={18} />} label="Total" value={products.length} />
        <Stat icon={<CheckCircle2 size={18} />} label="Valid" value={products.length - invalid.length} />
        <Stat icon={<AlertTriangle size={18} />} label="Invalid" value={invalid.length} danger />
        <Stat icon={<Filter size={18} />} label="Sources" value={sources} />

      </section>

      {/* TABLE */}
      <section className="table-card">

        <div className="table-inner" ref={tableInnerRef as any}>
          <table className="table">
          {/*hydration warns me to do this instead of the more readable top down commenting*/}
                           {/* Status            SKU                           Name FLEX                       Manufacturer                   Price                         Stock                        Source*/}
          <colgroup><col style={{ width: 80 }} /><col style={{ width: 140 }} /><col style={{ minWidth: 140 }}/><col style={{ width: 160 }} /><col style={{ width: 140 }} /><col style={{ width: 90 }} /><col style={{ width: 110 }} /></colgroup>

          <thead>
            <tr>
              <th className="sortable" onClick={() => changeSortAny('status')}>
                Status <span className="sort-indicator">{sortKey === 'status' ? (sortDir === 'asc' ? '▲' : '▼') : ''}</span>
              </th>
              <th className="sortable" onClick={() => changeSortAny('sku')}>
                SKU <span className="sort-indicator">{sortKey === 'sku' ? (sortDir === 'asc' ? '▲' : '▼') : ''}</span>
              </th>
              <th className="sortable" onClick={() => changeSortAny('name')}>
                Name <span className="sort-indicator">{sortKey === 'name' ? (sortDir === 'asc' ? '▲' : '▼') : ''}</span>
              </th>
              <th className="sortable" onClick={() => changeSortAny('manufacturer')}>
                Manufacturer <span className="sort-indicator">{sortKey === 'manufacturer' ? (sortDir === 'asc' ? '▲' : '▼') : ''}</span>
              </th>
              <th className="sortable" onClick={() => changeSortAny('finalPriceHuf')}>
                Price <span className="sort-indicator">{sortKey === 'finalPriceHuf' ? (sortDir === 'asc' ? '▲' : '▼') : ''}</span>
              </th>
              <th className="sortable" onClick={() => changeSortAny('stock')}>
                Stock <span className="sort-indicator">{sortKey === 'stock' ? (sortDir === 'asc' ? '▲' : '▼') : ''}</span>
              </th>
              <th className="sortable" onClick={() => changeSortAny('source')}>
                Source <span className="sort-indicator">{sortKey === 'source' ? (sortDir === 'asc' ? '▲' : '▼') : ''}</span>
              </th>
            </tr>
          </thead>

          <tbody>

            {loading && products.length === 0 && skeletonRows.map((_, i) => (
              <tr key={i}>
                <EllipsisTd><Skeleton circle /></EllipsisTd>
                <EllipsisTd><Skeleton /></EllipsisTd>
                <EllipsisTd><Skeleton wide /></EllipsisTd>
                <EllipsisTd><Skeleton /></EllipsisTd>
                <EllipsisTd><Skeleton /></EllipsisTd>
                <EllipsisTd><Skeleton small /></EllipsisTd>
                <EllipsisTd><Skeleton tag /></EllipsisTd>
              </tr>
            ))}

            {!loading && sortedProducts.map(p => (
              <tr key={p.sku} className={!p.valid ? 'invalid' : ''}>

                <td>
                  {p.valid
                    ? <CheckCircle2 size={18} className="ok" />
                    : (
                      <Tooltip text={p.validationErrors}>
                        <AlertTriangle size={18} className="bad" />
                      </Tooltip>
                    )
                  }
                </td>

                <EllipsisTd className="mono">{p.sku}</EllipsisTd>

                <EllipsisTd title={p.name}>{p.name}</EllipsisTd>

                <EllipsisTd>{p.manufacturer || '—'}</EllipsisTd>

                <EllipsisTd>
                  {p.finalPriceHuf
                    ? Math.round(p.finalPriceHuf).toLocaleString() + ' HUF'
                    : '—'
                  }
                </EllipsisTd>

                <EllipsisTd>{p.stock ?? 0}</EllipsisTd>

                <EllipsisTd>
                  <span className={`badge ${p.source.toLowerCase()}`}>
                    {p.source}
                  </span>
                </EllipsisTd>

              </tr>
            ))}

            {!loading && sortedProducts.length === 0 && (
              <tr className="empty-row">
                <EllipsisTd colSpan={7} className="empty">
                  <Filter size={40} />
                  <p>No products match filter</p>
                </EllipsisTd>
              </tr>
            )}

          </tbody>

          </table>
        </div>

      </section>

      {/* ISSUES */}
      {!onlyValid && invalid.length > 0 && (
        <section className="issues">

          <h2>
            <AlertTriangle size={18} />
            All Data Issues ({invalid.length})
          </h2>

          <div className="issue-grid">
            {invalid.map(p => (
              <div key={p.sku} className="issue-card">
                <strong>{p.sku}</strong>
                <span>{p.source}</span>
                <p>{p.validationErrors}</p>
              </div>
            ))}
          </div>

        </section>
      )}

    </div>
  );
};

export default App;
