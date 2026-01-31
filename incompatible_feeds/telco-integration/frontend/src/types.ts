export interface Product {
    sku: string;
    name: string;
    manufacturer: string;
    finalPriceHuf: number | null;
    stock: number | null;
    ean: string | null;
    updatedAt: string | null;
    source: string;
    valid: boolean;
    validationErrors: string;
}