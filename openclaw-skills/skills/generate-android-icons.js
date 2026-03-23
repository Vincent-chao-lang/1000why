/**
 * Android 图标生成技能
 */

const { execSync } = require('child_process');
const fs = require('fs-extra');
const path = require('path');

module.exports = {
  name: 'generate-android-icons',
  description: '从 SVG 源文件生成 Android 应用所需的所有尺寸图标',
  category: 'design',

  async execute(context) {
    const { svgPath, outputDir = './app/src/main/res' } = context;

    if (!svgPath) {
      throw new Error('请指定 SVG 文件路径');
    }

    const icons = [
      { size: 48, dir: 'mipmap-mdpi', name: 'ic_launcher' },
      { size: 72, dir: 'mipmap-hdpi', name: 'ic_launcher' },
      { size: 96, dir: 'mipmap-xhdpi', name: 'ic_launcher' },
      { size: 144, dir: 'mipmap-xxhdpi', name: 'ic_launcher' },
      { size: 192, dir: 'mipmap-xxxhdpi', name: 'ic_launcher' }
    ];

    let generated = [];

    for (const icon of icons) {
      const targetDir = path.join(outputDir, icon.dir);
      await fs.ensureDir(targetDir);
      const outputPath = path.join(targetDir, `${icon.name}.png`);
      
      try {
        execSync(`rsvg-convert -w ${icon.size} -h ${icon.size} "${svgPath}" "${outputPath}"`);
        generated.push(outputPath);
      } catch (e) {
        throw new Error('请安装 librsvg: brew install librsvg');
      }
    }

    return `✅ 图标生成完成！生成了 ${generated.length} 个图标`;
  }
};
