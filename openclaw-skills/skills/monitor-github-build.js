/**
 * GitHub 构建监控技能
 * 监控 GitHub Actions 构建状态
 */

const https = require('https');

module.exports = {
  name: 'monitor-github-build',
  description: '监控 GitHub Actions 构建状态，实时查看进度',
  category: 'devops',

  /**
   * 执行技能
   * @param {object} context - 上下文
   * @param {string} context.owner - 仓库所有者
   * @param {string} context.repo - 仓库名称
   */
  async execute(context) {
    const { owner = 'Vincent-chao-lang', repo = '1000why' } = context;

    // 获取最新构建状态
    const getBuildStatus = async () => {
      return new Promise((resolve, reject) => {
        https.get(`https://api.github.com/repos/${owner}/${repo}/actions/runs?per_page=1`, (res) => {
          let data = '';
          res.on('data', chunk => data += chunk);
          res.on('end', () => {
            try {
              const result = JSON.parse(data);
              const run = result.workflow_runs[0];
              resolve({
                status: run.status,
                conclusion: run.conclusion,
                url: run.html_url,
                name: run.name,
                id: run.id
              });
            } catch (e) {
              reject(e);
            }
          });
        });
      });
    };

    const build = await getBuildStatus();

    if (build.status === 'in_progress') {
      return `⏳ 构建进行中...\n\n状态: ${build.status}\nURL: ${build.url}\n\n我将持续监控构建进度...`;
    } else if (build.status === 'completed') {
      const emoji = build.conclusion === 'success' ? '✅' : '❌';
      return `${emoji} 构建${build.conclusion === 'success' ? '成功' : '失败'}\n\nURL: ${build.url}`;
    } else {
      return `⏳ 构建状态: ${build.status}\n\nURL: ${build.url}`;
    }
  }
};
