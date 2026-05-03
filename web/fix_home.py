with open('src/views/Home.vue', 'rb') as f:
    content = f.read()

# Fix the broken pattern - 好评？/span> -> 好评率</span>
# e5 a5 bd e8 af 84 e7 8e ? 2f 73 70 61 6e 3e -> e5 a5 bd e8 af 84 e7 8e 87 3c 2f 73 70 61 6e 3e

content = content.replace(
    b'\xe5\xa5\xbd\xe8\xaf\x84\xe7\x8e\x3f\x2f\x73\x70\x61\x6e\x3e',
    b'\xe5\xa5\xbd\xe8\xaf\x84\xe7\x8e\x87\x3c\x2f\x73\x70\x61\x6e\x3e'
)

with open('src/views/Home.vue', 'wb') as f:
    f.write(content)

print('Fixed Home.vue')
