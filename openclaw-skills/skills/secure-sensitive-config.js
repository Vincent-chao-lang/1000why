/**
 * 保护敏感配置技能
 * 保护项目中的敏感配置信息
 */

const fs = require('fs-extra');
const path = require('path');

module.exports = {
  name: 'secure-sensitive-config',
  description: '保护项目中的敏感配置信息，使用模板文件 + .gitignore 隔离',
  category: 'security',

  async execute(context) {
    const { projectPath = '.', sensitiveFiles = [] } = context;

    // 默认敏感文件列表
    const defaultSensitiveFiles = [
      'keystore.properties',
      'keystore.jks',
      '*.jks',
      'secrets.config',
      '.env'
    ];

    const filesToSecure = [...defaultSensitiveFiles, ...sensitiveFiles];

    // 更新 .gitignore
    const gitignorePath = path.join(projectPath, '.gitignore');
    let gitignore = '';
    
    if (await fs.pathExists(gitignorePath)) {
      gitignore = await fs.readFile(gitignorePath, 'utf8');
    }

    const newEntries = filesToSecure.map(f => `# ${f} (contains sensitive data)\n${f}\n`).join('\n');

    // 检查是否已存在
    const needsUpdate = !gitignore.includes('# 敏感配置保护');
    
    if (needsUpdate) {
      await fs.writeFile(gitignorePath, `${gitignore}\n${newEntries}`);
    }

    // 从 git 移除已跟踪的敏感文件
    for (const file of filesToSecure) {
      try {
        execSync(`cd ${projectPath} && git rm --cached ${file} 2>/dev/null || true`);
      } catch (e) {
        // ignore errors
      }
    }

    return `✅ 敏感配置保护完成！\n\n已添加到 .gitignore:\n${filesToSecure.map(f => `- ${f}`).join('\n')}\n\n下一步：\n1. 创建模板文件：mv config.properties config.properties.template\n2. 本地配置：cp config.properties.template config.properties\n3. 提交 git：git add .gitignore && git commit -m "保护敏感配置"`;
  }
};
