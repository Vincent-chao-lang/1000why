/**
 * OpenClaw Skills 使用示例
 *
 * 这个文件演示了如何在 OpenClaw 中调用技能
 * 基于用户的自然语言描述，OpenClaw 会自动匹配并执行对应技能
 */

const { executeSkill, listSkills } = require('../index');

/**
 * 示例 1: 完整的项目创建流程
 * 用户说："帮我创建一个 AI手机助手 Launcher 应用"
 */
async function exampleCreateLauncher() {
  console.log('\n=== 示例 1: 创建 Launcher 应用 ===\n');

  await executeSkill('create-launcher-app', {
    projectPath: './MyAILauncher',
    targetPackage: 'com.larus.nova',  // 豆包 AI
    appName: 'AI手机助手',
    packageName: 'com.launcher.home'
  });
}

/**
 * 示例 2: 为现有项目添加反馈功能
 * 用户说："在 download.html 添加用户反馈表单"
 */
async function exampleAddFeedback() {
  console.log('\n=== 示例 2: 添加反馈表单 ===\n');

  await executeSkill('add-feedback-form', {
    htmlFilePath: './download.html',
    email: '2819699195@qq.com'
  });
}

/**
 * 示例 3: 配置 CI/CD
 * 用户说："配置这个项目的 GitHub Actions"
 */
async function exampleSetupCI() {
  console.log('\n=== 示例 3: 配置 GitHub Actions CI/CD ===\n');

  await executeSkill('setup-android-ci', {
    projectPath: '.',
    packageName: 'com.launcher.home',
    minSdk: 26,
    targetSdk: 35,
    appName: 'AI手机助手'
  });
}

/**
 * 示例 4: 生成应用图标
 * 用户说："从 icon.svg 生成所有尺寸的图标"
 */
async function exampleGenerateIcons() {
  console.log('\n=== 示例 4: 生成应用图标 ===\n');

  await executeSkill('generate-android-icons', {
    svgPath: 'icon.svg',
    outputDir: './app/src/main/res'
  });
}

/**
 * 示例 5: 查看构建状态
 * 用户说："查看当前 GitHub 构建状态"
 */
async function exampleMonitorBuild() {
  console.log('\n=== 示例 5: 监控 GitHub 构建 ===\n');

  await executeSkill('monitor-github-build', {
    owner: 'Vincent-chao-lang',
    repo: '1000why'
  });
}

/**
 * 示例 6: 生成技术文档
 * 用户说："为项目生成技术文档"
 */
async function exampleCreateDoc() {
  console.log('\n=== 示例 6: 生成技术文档 ===\n');

  await executeSkill('create-tech-doc', {
    projectPath: '.',
    projectName: 'AI手机助手',
    projectType: 'Android Launcher'
  });
}

/**
 * 示例 7: 保护敏感配置
 * 用户说："保护项目中的敏感配置文件"
 */
async function exampleSecureConfig() {
  console.log('\n=== 示例 7: 保护敏感配置 ===\n');

  await executeSkill('secure-sensitive-config', {
    projectPath: '.',
    sensitiveFiles: [
      'keystore.properties',
      'api-keys.properties',
      'local.config'
    ]
  });
}

/**
 * 完整工作流示例
 * 用户说："从头创建一个完整的 Android Launcher 项目"
 */
async function exampleCompleteWorkflow() {
  console.log('\n=== 完整工作流：创建 Launcher 项目 ===\n');

  try {
    // 1. 创建 Launcher 应用
    console.log('步骤 1: 创建应用结构...');
    await executeSkill('create-launcher-app', {
      projectPath: './MyAILauncher',
      targetPackage: 'com.larus.nova',
      appName: 'AI手机助手',
      packageName: 'com.launcher.home'
    });

    // 2. 生成图标
    console.log('\n步骤 2: 生成应用图标...');
    await executeSkill('generate-android-icons', {
      svgPath: './MyAILauncher/icon.svg',
      outputDir: './MyAILauncher/app/src/main/res'
    });

    // 3. 配置 CI/CD
    console.log('\n步骤 3: 配置 GitHub Actions...');
    await executeSkill('setup-android-ci', {
      projectPath: './MyAILauncher',
      packageName: 'com.launcher.home',
      appName: 'AI手机助手'
    });

    // 4. 保护配置
    console.log('\n步骤 4: 保护敏感配置...');
    await executeSkill('secure-sensitive-config', {
      projectPath: './MyAILauncher'
    });

    // 5. 生成文档
    console.log('\n步骤 5: 生成技术文档...');
    await executeSkill('create-tech-doc', {
      projectPath: './MyAILauncher',
      projectName: 'AI手机助手',
      projectType: 'Android Launcher'
    });

    console.log('\n✅ 项目创建完成！\n');

  } catch (error) {
    console.error('❌ 错误:', error.message);
  }
}

/**
 * 列出所有可用技能
 */
function exampleListSkills() {
  console.log('\n=== 可用技能列表 ===\n');

  const skills = listSkills();

  skills.forEach(skill => {
    console.log(`📦 ${skill.name}`);
    console.log(`   分类: ${skill.category}`);
    console.log(`   描述: ${skill.description}`);
    console.log('');
  });
}

// 导出示例函数
module.exports = {
  exampleCreateLauncher,
  exampleAddFeedback,
  exampleSetupCI,
  exampleGenerateIcons,
  exampleMonitorBuild,
  exampleCreateDoc,
  exampleSecureConfig,
  exampleCompleteWorkflow,
  exampleListSkills
};

// 如果直接运行此文件
if (require.main === module) {
  console.log(`
╔════════════════════════════════════════════════════════════╗
║          OpenClaw Android Skills 使用示例                  ║
╚════════════════════════════════════════════════════════════╝
  `);

  exampleListSkills();

  console.log(`
═════════════════════════════════════════════════════════════
使用方法：
  node test/example-usage.js        # 列出所有技能
  node test/example-usage.js <示例名>  # 运行特定示例
═════════════════════════════════════════════════════════════
  `);

  // 运行特定示例（如果通过参数指定）
  const exampleName = process.argv[2];
  if (exampleName) {
    const exampleFn = module.exports[`example${exampleName}`];
    if (exampleFn) {
      exampleFn().catch(console.error);
    } else {
      console.log(`❌ 未找到示例: ${exampleName}`);
      console.log('可用示例:', Object.keys(module.exports).filter(k => k.startsWith('example')));
    }
  }
}
