/**
 * 反馈表单集成技能
 * 在 HTML 页面中集成 Formsubmit.co 反馈表单
 */

const fs = require('fs-extra');
const path = require('path');

module.exports = {
  name: 'add-feedback-form',
  description: '在 HTML 页面中集成 Formsubmit.co 反馈表单，无需登录即可发送邮件',
  category: 'web',

  /**
   * 执行技能
   * @param {object} context - 上下文
   * @param {string} context.htmlFilePath - HTML 文件路径
   * @param {string} context.email - 接收邮箱
   */
  async execute(context) {
    const { htmlFilePath, email = 'YOUR_EMAIL@example.com' } = context;

    if (!htmlFilePath) {
      throw new Error('请指定 HTML 文件路径');
    }

    const html = await fs.readFile(htmlFilePath, 'utf8');

    // 表单 HTML
    const formHTML = `
    <section class="feedback-section">
        <h2>💬 问题反馈</h2>
        <p class="subtitle">遇到问题或有建议？欢迎留言反馈</p>

        <form class="feedback-form" action="https://formsubmit.co/${email}" method="POST">
            <input type="hidden" name="_subject" value="新反馈">
            <input type="hidden" name="_captcha" value="false">
            <input type="text" name="_honey" style="display:none">

            <div class="form-group">
                <label>您的称呼 *</label>
                <input type="text" name="name" required placeholder="请输入您的称呼">
            </div>

            <div class="form-group">
                <label>联系邮箱</label>
                <input type="email" name="email" placeholder="可选，用于接收回复">
            </div>

            <div class="form-group">
                <label>反馈类型 *</label>
                <select name="type" required>
                    <option value="">请选择反馈类型</option>
                    <option value="bug">🐛 问题报告</option>
                    <option value="feature">💡 功能建议</option>
                    <option value="question">❓ 使用咨询</option>
                    <option value="other">📝 其他</option>
                </select>
            </div>

            <div class="form-group">
                <label>详细描述 *</label>
                <textarea name="message" required placeholder="请详细描述您遇到的问题或建议..."></textarea>
            </div>

            <button type="submit" class="submit-btn">提交反馈</button>
        </form>
    </section>
`;

    // 在 </body> 前插入表单
    const updatedHtml = html.replace('</body>', `${formHTML}\n</body>`);

    await fs.writeFile(htmlFilePath, updatedHtml);

    return `✅ 反馈表单已添加到 ${htmlFilePath}\n\n下一步：\n1. 将 ${email} 替换为你的真实邮箱\n2. 首次提交后会收到验证邮件`;
  }
};
