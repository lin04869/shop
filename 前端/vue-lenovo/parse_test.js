function parseGoodsInfo(name) {
  if (!name) return { os: '', configuration: '', color: '' };
  
  const normalizedForSegment = name
    .replace(/[、，,；;|]/g, '/')
    .replace(/\s*\/\s*/g, '/')
    .trim();
  const segments = normalizedForSegment.split('/').map(s => s.trim()).filter(Boolean);

  const normalized = name.replace(/[、，,；;|]/g, '/').replace(/\s*\/\s*/g, '/').replace(/\s+/g, ' ').trim();
  const tokens = normalized.split(/[\/\s]+/).map(t => t.trim()).filter(Boolean);
  
  const osRegex = /(win(?:dows)?(?:\s*(?:10|11))?|windows(?:\s*(?:10|11))?|win10|win11|linux|mac(?:os)?|macos|android|ios|鸿蒙|ubuntu|centos)/i;
  const colors = ['黑', '白', '灰', '蓝', '红', '银', '金', '绿', '粉'];
  const modifiers = ['深', '浅', '炫彩', '渐变'];

  let os = '', configuration = '', color = '';

  // 先提取整段颜色
  if (!color) {
    for (const seg of segments) {
      // 段落包含颜色/修饰符关键字，取整段作为颜色
      if (colors.some(c => seg.includes(c)) || modifiers.some(m => seg.includes(m))) {
        color = seg;
        break;
      }
    }
  }

  for (let i = 0; i < tokens.length; i++) {
    const t = tokens[i];
    const osMatch = t.match(osRegex);
    if (!os && osMatch) {
      let phraseTokens = [];
      const prev = tokens[i - 1] || '';
      if (/^(预装|带|含|原装)$/i.test(prev)) phraseTokens.push(prev);
      phraseTokens.push(t);
      const suffixPattern = /^(家庭版|家庭中文版|家庭|专业版|旗舰版|中文版|简体中文版|Pro|Home|64位|32位|64-bit|32-bit|x64|x86|简体)$/i;
          // Identify screen size tokens
          const sizePattern = /^\d{1,2}(?:\.\d+)?(?:英寸|寸|inch|in|\")?$/i;
      const cfgPattern = /(i[3579]|ryzen|intel|amd|\d+GB|\d+TB|SSD|HDD|MX|RTX|GTX|16G|32G|512G|1TB|2TB)/i;
      for (let j = i + 1; j < Math.min(tokens.length, i + 6); j++) {
        const next = tokens[j];
        if (cfgPattern.test(next)) break;
        if (sizePattern.test(next)) break;
        if (suffixPattern.test(next) || /\p{Script=Han}/u.test(next) || /^[0-9]+$/.test(next)) {
          phraseTokens.push(next); i = j;
        } else {
          break;
        }
      }
      let matched = phraseTokens.join(' ').trim();
      matched = matched.replace(/^win(?:dows)?/i, 'Windows');
      matched = matched.replace(/^(win10|win11)/i, (m) => `Windows ${m.replace(/\D/g, '')}`);
      matched = matched.replace(/^(mac|macos)/i, 'macOS');
      os = matched;
      continue;
    }

    if (!segments.includes(t) || (segments.includes(t) && t !== color)) {
      configuration = configuration ? (configuration + ' / ' + t) : t;
    }
  }

  return { os, configuration, color };
}

const tests = [
  'Windows 10 / i5 / 浅灰',
  '预装 Win10 家庭版 8GB 512G',
  'Windows 11 家庭版 15.6英寸 / i7 / 16GB / 512GB',
  'win11 家庭中文版 15.6" i5 8GB',
  'macOS 10.15 / 银色',
  '三星 Android 11 浅黑',
  'Ryzen 5 / 深灰',
  '联想-预装win11-16G-深灰',
  'AMD Ryzen 9 8945HX/Windows 11 家庭中文版/16英寸/16GB/1T SSD/ RTX™ 5060 8GB独显/碳晶黑'
];

tests.forEach(t => {
  console.log('Input:', t);
  console.log(parseGoodsInfo(t));
  console.log('---------------------');
});