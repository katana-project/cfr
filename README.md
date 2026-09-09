# cfr

A JavaScript port of the [CFR decompiler](https://github.com/leibnitz27/cfr).

## Example

```js
const fs = require("fs");
const { decompile } = require("./cfr.js"); // get it from the dist/ directory or jsDelivr

const data = fs.readFileSync("./your/package/HelloWorld.class"); // read a class file
console.log(await decompile("your/package/HelloWorld", {
    source: async (name) => {
        /* provide classes for analysis here, including the one you want to decompile */
        
        console.log(name); /* internal name, e.g. java/lang/Object */
        return name === "your/package/HelloWorld" ? data : null /* class not available */;
    },
    options: {
        /* see https://github.com/leibnitz27/cfr/blob/master/src/org/benf/cfr/reader/util/getopt/OptionsImpl.java#L274 */
        "hidelangimports": "false", /* testing option - don't hide java.lang imports */
    },
})["your/package/HelloWorld"]);
```

## Licensing

The supporting code for this project and the CFR decompiler are licensed under the MIT License
([supporting code](./LICENSE), [CFR](./LICENSE-CFR)).

_This project is not affiliated with, maintained or endorsed by the CFR project in any way. Do NOT report issues with this project to the CFR issue tracker._
