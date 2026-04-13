file_path = r'd:\projects\worry-free-road-research\材料\前后台.md'

with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

content = content.replace('### 5.3.3 首页统计', '### 5.3.2 首页统计')

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)

print("Fixed header.")
