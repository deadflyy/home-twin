const fs = require('fs');
const path = require('path');

const iconDir = path.join(__dirname, '../src/static/icons');

if (!fs.existsSync(iconDir)) {
  fs.mkdirSync(iconDir, { recursive: true });
}

const createSimplePNG = (width, height, r, g, b, a = 255) => {
  const signature = Buffer.from([137, 80, 78, 71, 13, 10, 26, 10]);
  
  const ihdrData = Buffer.alloc(13);
  ihdrData.writeUInt32BE(width, 0);
  ihdrData.writeUInt32BE(height, 4);
  ihdrData[8] = 8;
  ihdrData[9] = 6;
  ihdrData[10] = 0;
  ihdrData[11] = 0;
  ihdrData[12] = 0;
  
  const ihdrCrc = crc32(Buffer.concat([Buffer.from('IHDR'), ihdrData]));
  const ihdrChunk = Buffer.concat([
    Buffer.from([0, 0, 0, 13]),
    Buffer.from('IHDR'),
    ihdrData,
    intToBuffer(ihdrCrc)
  ]);
  
  const rawData = [];
  for (let y = 0; y < height; y++) {
    rawData.push(0);
    for (let x = 0; x < width; x++) {
      rawData.push(r, g, b, a);
    }
  }
  
  const zlib = require('zlib');
  const compressed = zlib.deflateSync(Buffer.from(rawData));
  
  const idatCrc = crc32(Buffer.concat([Buffer.from('IDAT'), compressed]));
  const idatChunk = Buffer.concat([
    intToBuffer(compressed.length),
    Buffer.from('IDAT'),
    compressed,
    intToBuffer(idatCrc)
  ]);
  
  const iendChunk = Buffer.from([0, 0, 0, 0, 73, 69, 78, 68, 174, 66, 96, 130]);
  
  return Buffer.concat([signature, ihdrChunk, idatChunk, iendChunk]);
};

const crc32 = (buf) => {
  let crc = 0xffffffff;
  const table = [];
  
  for (let i = 0; i < 256; i++) {
    let c = i;
    for (let j = 0; j < 8; j++) {
      c = (c & 1) ? (0xedb88320 ^ (c >>> 1)) : (c >>> 1);
    }
    table[i] = c;
  }
  
  for (let i = 0; i < buf.length; i++) {
    crc = table[(crc ^ buf[i]) & 0xff] ^ (crc >>> 8);
  }
  
  return (crc ^ 0xffffffff) >>> 0;
};

const intToBuffer = (val) => {
  const buf = Buffer.alloc(4);
  buf.writeUInt32BE(val, 0);
  return buf;
};

const icons = [
  { name: 'home.png', color: [153, 153, 153] },
  { name: 'home-active.png', color: [74, 144, 217] },
  { name: 'search.png', color: [153, 153, 153] },
  { name: 'search-active.png', color: [74, 144, 217] },
  { name: 'stats.png', color: [153, 153, 153] },
  { name: 'stats-active.png', color: [74, 144, 217] }
];

icons.forEach(icon => {
  const png = createSimplePNG(48, 48, icon.color[0], icon.color[1], icon.color[2]);
  fs.writeFileSync(path.join(iconDir, icon.name), png);
  console.log(`Created ${icon.name}`);
});

console.log('All icons generated successfully!');