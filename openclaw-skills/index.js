/**
 * OpenClaw Skills 主入口
 * 用于管理和注册所有 Android 开发相关的技能
 */

const fs = require('fs-extra');
const path = require('path');

// 技能注册表
const skills = {
  'setup-android-ci': require('./skills/setup-android-ci'),
  'add-feedback-form': require('./skills/add-feedback-form'),
  'create-launcher-app': require('./skills/create-launcher-app'),
  'monitor-github-build': require('./skills/monitor-github-build'),
  'generate-android-icons': require('./skills/generate-android-icons'),
  'create-tech-doc': require('./skills/create-tech-doc'),
  'secure-sensitive-config': require('./skills/secure-sensitive-config')
};

/**
 * 执行指定技能
 * @param {string} skillName - 技能名称
 * @param {object} context - 执行上下文
 * @returns {Promise<string>} 执行结果
 */
async function executeSkill(skillName, context) {
  if (!skills[skillName]) {
    throw new Error(`技能 "${skillName}" 不存在`);
  }

  console.log(`🎯 执行技能: ${skillName}`);
  const result = await skills[skillName].execute(context);
  console.log(`✅ ${result}`);
  return result;
}

/**
 * 列出所有可用技能
 * @returns {Array} 技能列表
 */
function listSkills() {
  return Object.entries(skills).map(([name, skill]) => ({
    name,
    description: skill.description,
    category: skill.category || 'general'
  }));
}

module.exports = {
  executeSkill,
  listSkills,
  skills
};
