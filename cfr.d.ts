export type Options = Record<string, string>;

export interface Config {
    source?: (name: string) => Promise<Uint8Array | null>;
    options?: Options;
}

export declare const decompile: (names: string | string[], config?: Config) => Promise<string>;
