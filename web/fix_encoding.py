import re

with open('src/views/NewsList.vue', 'r', encoding='utf-8') as f:
    content = f.read()

# Fix broken tags
content = content.replace('排序</span>', '排序</span>')
content = content.replace('按发布时间" value="createTime"', '按发布时间" value="createTime"')

# Find and fix the broken lines
lines = content.split('\n')
for i, line in enumerate(lines):
    if 'sort-label' in line and '</span>' not in line:
        lines[i] = '            <span class="sort-label">排序</span>'
    if 'el-option label="按发布时间' in line and 'value=' not in line:
        lines[i] = '              <el-option label="按发布时间" value="createTime" />'

with open('src/views/NewsList.vue', 'w', encoding='utf-8') as f:
    f.write('\n'.join(lines))

print('Fixed NewsList.vue')
