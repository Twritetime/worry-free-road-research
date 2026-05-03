with open('src/views/NewsList.vue', 'rb') as f:
    content = f.read()

# Fix the broken el-option - replace 按发布时间？ value= with 按发布时间" value=
# The broken bytes are: e6 8c 89 e5 8f 91 e5 b8 83 e6 97 b6 e9 97 3f 20 76 61 6c 75 65 3d
# Should be: e6 8c 89 e5 8f 91 e5 b8 83 e6 97 b6 e9 97 b4 22 20 76 61 6c 75 65 3d

content = content.replace(
    b'\xe6\x8c\x89\xe5\x8f\x91\xe5\xb8\x83\xe6\x97\xb6\xe9\x97\x3f\x20\x76\x61\x6c\x75\x65\x3d',
    b'\xe6\x8c\x89\xe5\x8f\x91\xe5\xb8\x83\xe6\x97\xb6\xe9\x97\xb4\x22\x20\x76\x61\x6c\x75\x65\x3d'
)

with open('src/views/NewsList.vue', 'wb') as f:
    f.write(content)

print('Fixed NewsList.vue el-option')
