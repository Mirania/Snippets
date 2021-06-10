import * as sharp from "sharp";
import { Sharp, Metadata } from "sharp";

type Color = { red: number, green: number, blue: number };

// The size cap of the images.
const sizeCap = 300;

// Proportions used for calcSignature.
const sigProps = [1/10, 3/10, 5/10, 7/10, 9/10];

/**
 * Returns the difference between images as a numeric value.
 * The closer to 0 the value is, the more similar the images are.
 * A value of 0 means the images are equal.
 */
export async function compare(a: string | Buffer, b: string | Buffer): Promise<number> {
    return await scaleAndCompare(sharp(a), sharp(b));
}

async function scaleAndCompare(imgX: Sharp, imgY: Sharp): Promise<number> {
    const scaledSize = await getAppropriateScale(imgX, imgY);
    const scaledX = await rescale(imgX, scaledSize, scaledSize, true);
    const scaledY = await rescale(imgY, scaledSize, scaledSize, true);
    return calcDistance(scaledX, scaledY, scaledSize);
}

async function calcSignature(img: Sharp, scaledSize: number): Promise<Color[][]> {
    // Get memory for the signature.
    const sig: Color[][] = [[], [], [], [], []];
    // For each of the 25 signature values average the pixels around it.
    // Note that the coordinate of the central pixel is in proportions.
    for (let x = 0; x < 5; x++) {
        for (let y = 0; y < 5; y++) {
            sig[x][y] = await averageAround(img, scaledSize, sigProps[x], sigProps[y]);
        }
    }
    return sig;
}

async function calcDistance(imgX: Sharp, imgY: Sharp, scaledSize: number): Promise<number> {
    // Calculate the signature for the images.
    const sigX = await calcSignature(imgX, scaledSize);
    const sigY = await calcSignature(imgY, scaledSize);
    // There are several ways to calculate distances between two vectors,
    // we will calculate the sum of the distances between the RGB values of
    // pixels in the same positions.
    let dist = 0;
    for (let x = 0; x < 5; x++) {
        for (let y = 0; y < 5; y++) {
            let r1 = sigX[x][y].red;
            let g1 = sigX[x][y].green;
            let b1 = sigX[x][y].blue;
            let r2 = sigY[x][y].red;
            let g2 = sigY[x][y].green;
            let b2 = sigY[x][y].blue;
            dist += Math.sqrt((r1 - r2) * (r1 - r2) + (g1 - g2) * (g1 - g2) + (b1 - b2) * (b1 - b2));
        }
    }
    return dist;
}

async function averageAround(img: Sharp, scaledSize: number, px: number, py: number): Promise<Color> {
    // Get memory for a pixel and for the accumulator.
    const accumulator = { red: 0, green: 0, blue: 0 };
    // The size of the sampling area.
    const sampleSize = 0.09 * scaledSize;
    const metadata = await img.metadata();
    const buffer = await img.raw().toBuffer();
    let numPixels = 0;
    // Sample the pixels.
    for (let x = px * scaledSize - sampleSize; x < px * scaledSize + sampleSize; x++) {
        for (let y = py * scaledSize - sampleSize; y < py * scaledSize + sampleSize; y++) {
            const pixel = colorFromPixel(buffer, metadata, Math.floor(x), Math.floor(y));
            accumulator.red += pixel.red;
            accumulator.green += pixel.green;
            accumulator.blue += pixel.blue;
            numPixels++;
        }
    }
    // Average the accumulated values.
    accumulator.red /= numPixels;
    accumulator.green /= numPixels;
    accumulator.blue /= numPixels;
    return { 
        red: Math.floor(accumulator.red), 
        green: Math.floor(accumulator.green),
        blue: Math.floor(accumulator.blue)
    };
}

function colorFromPixel(buffer: Buffer, metadata: Metadata, x: number, y: number): Color {
    const offset = (y * metadata.width + x) * (metadata.hasAlpha ? 4 : 3);
    return { 
        red: buffer.readUInt8(offset), 
        green: buffer.readUInt8(offset + 1),
        blue: buffer.readUInt8(offset + 2)
    };
}

async function getAppropriateScale(imgX: Sharp, imgY: Sharp): Promise<number> {
    const metadataX = await imgX.metadata();
    const metadataY = await imgY.metadata();
    return Math.min(sizeCap, metadataX.width, metadataX.height, metadataY.width, metadataY.height);
}

async function rescale(img: Sharp, 
                       targetWidth: number, 
                       targetHeight: number, 
                       higherQuality: boolean): Promise<Sharp> {
    let ret = img;
    let w: number, h: number;

    if (higherQuality) {
        // Use multi-step technique: start with original size, then
        // scale down in multiple passes with drawImage()
        // until the target size is reached
        const metadata = await img.metadata();
        w = metadata.width;
        h = metadata.height;
    } else {
        // Use one-step technique: scale directly from original
        // size to target size with a single drawImage() call
        w = targetWidth;
        h = targetHeight;
    }

    do {
        if (higherQuality && w < targetWidth) {
            w *= 1.2;
        }

        if (higherQuality && h < targetHeight) {
            h *= 1.2;
        }

        if (higherQuality && w > targetWidth) {
            w /= 2.5;
            if (w < targetWidth) {
                w = targetWidth;
            }
        }

        if (higherQuality && h > targetHeight) {
            h /= 2.5;
            if (h < targetHeight) {
                h = targetHeight;
            }
        }

        ret = sharp(await img.resize({width: Math.floor(w), height: Math.floor(h), fit: "fill"}).toBuffer());
    } while (w !== targetWidth || h !== targetHeight);

    return sharp(await ret.toBuffer());
}