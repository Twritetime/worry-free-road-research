import re

file_path = r'd:\projects\worry-free-road-research\材料\前后台.md'

with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

# 1. 补充后台登录的文本
login_content = """后台登录模块是管理员进入系统后台的唯一入口，用于验证管理员身份并根据其角色分配权限，从而保障后台管理系统的安全性。登录界面设计简洁专业，管理员需输入正确的用户名和密码进行身份验证。为了方便从前台直接跳转，界面底部还提供了“前台登录”和“返回首页”的快捷链接。系统后台登录界面的展示效果如图60所示。

图60 系统后台登录图

后端通过统一的 `login` 接口处理管理员的登录状态验证，具体实现逻辑如下：首先接收前端传入的表单参数，调用 `userService.login` 方法验证用户名和密码的正确性；验证通过后，系统会进一步检查该用户的角色，只有当角色为 `ADMIN`（超级管理员）或 `OPERATOR`（运营人员）时，才允许其登入后台系统，否则会拦截并提示“当前账号无后台权限”；权限校验通过后，系统会使用 `tokenService` 为该管理员生成唯一的鉴权 Token，并将其信息存储在前端的 Pinia 状态管理库（`userStore`）中，后续的所有后台请求均需携带该 Token 以供 Spring Security 过滤器进行身份识别。具体实现代码如图61所示。

图61 后台登录实现代码图
"""

content = re.sub(r'### 5\.3\.1 后台登录\n+图60 系统后台登录图\n+图61 后台登录实现代码图\n+', '### 5.3.1 后台登录\n\n' + login_content + '\n', content)

# 分离出后台登录部分和后面的部分，以免影响后台登录的图号
parts = content.split('### 5.3.1 首页统计')
if len(parts) == 2:
    header_part = parts[0]
    rest_part = '### 5.3.2 首页统计' + parts[1]
    
    # 替换后面的 section numbers (5.3.2 to 5.3.3, etc)
    # Be careful to iterate from 7 down to 2 to avoid double replacing
    for i in range(7, 1, -1):
        rest_part = rest_part.replace(f'### 5.3.{i}', f'### 5.3.{i+1}')
        
    # 替换后面的 图号 (60 to 62, etc)
    for i in range(79, 59, -1):
        rest_part = rest_part.replace(f'图{i}', f'图{i+2}')
        
    content = header_part + rest_part

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)

print("Update completed.")
