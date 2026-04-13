import re
import os

file_path = r"d:\projects\worry-free-road-research\材料\初稿.md"

try:
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()

    # Define the replacements map
    replacements = {
        r'\*\*\(1\) 用户实体\*\*：.*?等，用户实体属性图如图7所示：': r'**(1) 用户实体**：包含属性 id、账号 (username)、密码 (password)、昵称 (nickname)、邮箱 (email)、电话 (phone)、头像 (avatar)、角色 (role)、创建时间 (create_time)、更新时间 (update_time)、逻辑删除 (deleted)。用户实体属性图如图7所示：',
        
        r'\*\*\(2\) 资讯实体\*\*：.*?等，资讯实体属性图如图8所示：': r'**(2) 资讯实体**：包含属性 id、标题 (title)、内容 (content)、类型 (type)、封面图 (cover_img)、浏览量 (view_count)、状态 (status)、评论数 (comment_count)、创建时间 (create_time)、更新时间 (update_time)、逻辑删除 (deleted)。资讯实体属性图如图8所示：',
        
        r'\*\*\(3\) 指南实体\*\*：.*?等，指南实体属性图如图9所示：': r'**(3) 指南实体**：包含属性 id、标题 (title)、内容 (content)、分类 (category)、封面图 (cover_img)、浏览量 (view_count)、状态 (status)、报考院校 (institution)、报考专业 (major)、评论数 (comment_count)、创建时间 (create_time)、更新时间 (update_time)、逻辑删除 (deleted)。指南实体属性图如图9所示：',
        
        r'\*\*\(4\) 资料实体\*\*：.*?等，资料实体属性图如图10所示：': r'**(4) 资料实体**：包含属性 id、名称 (name)、描述 (description)、价格 (price)、库存 (stock)、销量 (sales)、分类 (category)、封面图 (cover_img)、文件路径 (file_url)、规格 (specs)、状态 (status)、创建时间 (create_time)、更新时间 (update_time)、逻辑删除 (deleted)。资料实体属性图如图10所示：',
        
        r'\*\*\(5\) 收货地址实体\*\*：.*?等，收货地址实体属性图如图11所示：': r'**(5) 收货地址实体**：包含属性 id、用户ID (user_id)、收货人姓名 (receiver_name)、收货人电话 (receiver_phone)、省份 (province)、城市 (city)、区县 (district)、详细地址 (detail_address)、是否默认 (is_default)、创建时间 (create_time)、更新时间 (update_time)、逻辑删除 (deleted)。收货地址实体属性图如图11所示：',
        
        r'\*\*\(6\) 订单实体\*\*：.*?等，订单实体属性图如图12所示：': r'**(6) 订单实体**：包含属性 id、订单编号 (order_no)、用户ID (user_id)、总金额 (total_amount)、状态 (status)、创建时间 (create_time)、更新时间 (update_time)、逻辑删除 (deleted)。订单实体属性图如图12所示：',
        
        r'\*\*\(7\) 订单项实体\*\*：.*?等，订单项实体属性图如图13所示：': r'**(7) 订单项实体**：包含属性 id、订单ID (order_id)、资料ID (material_id)、资料名称 (material_name)、价格 (price)、数量 (quantity)、创建时间 (create_time)、更新时间 (update_time)、逻辑删除 (deleted)。订单项实体属性图如图13所示：',
        
        r'\*\*\(8\) 购物车项实体\*\*：.*?等，购物车项实体属性图如图14所示：': r'**(8) 购物车项实体**：包含属性 id、用户ID (user_id)、资料ID (material_id)、数量 (quantity)、创建时间 (create_time)、更新时间 (update_time)、逻辑删除 (deleted)。购物车项实体属性图如图14所示：',
        
        r'\*\*\(9\) 帖子实体\*\*：.*?等，帖子实体属性图如图15所示：': r'**(9) 帖子实体**：包含属性 id、标题 (title)、内容 (content)、作者ID (user_id)、作者昵称 (nickname)、作者头像 (avatar)、浏览量 (view_count)、点赞数 (like_count)、评论数 (comment_count)、板块分类 (category)、状态 (status)、是否置顶 (is_top)、创建时间 (create_time)、更新时间 (update_time)、逻辑删除 (deleted)。帖子实体属性图如图15所示：',
        
        r'\*\*\(10\) 评论实体\*\*：.*?等，评论实体属性图如图16所示：': r'**(10) 评论实体**：包含属性 id、关联帖子ID (post_id)、内容 (content)、用户ID (user_id)、用户昵称 (nickname)、用户头像 (avatar)、目标类型 (target_type)、目标ID (target_id)、父评论ID (parent_id)、被回复用户ID (reply_to_user_id)、被回复用户昵称 (reply_to_nickname)、创建时间 (create_time)、更新时间 (update_time)、逻辑删除 (deleted)。评论实体属性图如图16所示：',
        
        r'\*\*\(11\) 收藏实体\*\*：.*?等，收藏实体属性图如图17所示：': r'**(11) 收藏实体**：包含属性 id、用户ID (user_id)、目标类型 (target_type)、目标ID (target_id)、目标标题 (target_title)、目标封面 (target_cover)、创建时间 (create_time)、更新时间 (update_time)、逻辑删除 (deleted)。收藏实体属性图如图17所示：',
        
        r'\*\*\(12\) 意见反馈实体\*\*：.*?等，意见反馈实体属性图如图18所示：': r'**(12) 意见反馈实体**：包含属性 id、用户ID (user_id)、反馈内容 (content)、回复内容 (reply)、状态 (status)、回复时间 (reply_time)、创建时间 (create_time)、更新时间 (update_time)、逻辑删除 (deleted)。意见反馈实体属性图如图18所示：',
        
        r'\*\*\(13\) 首页配置实体\*\*：.*?等，首页配置实体属性图如图19所示：': r'**(13) 首页配置实体**：包含属性 id、类型 (type)、标题 (title)、图片链接 (img_url)、跳转链接 (link_url)、排序 (sort_order)、状态 (status)、创建时间 (create_time)、更新时间 (update_time)、逻辑删除 (deleted)。首页配置实体属性图如图19所示：'
    }

    new_content = content
    for pattern, replacement in replacements.items():
        new_content = re.sub(pattern, replacement, new_content, flags=re.DOTALL)

    if new_content == content:
        print("Warning: No changes were made. Regex might not have matched.")
    else:
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(new_content)
        print("Successfully updated entity descriptions.")

except Exception as e:
    print(f"Error: {e}")
