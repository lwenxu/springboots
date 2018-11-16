# 功能
该目录为项目构建的脚本文件，主要为 Dockerfile 文件 以及构建的 shell 脚本

# 部署项目
1. 首先需要给予 start.sh 执行权限
2. 执行 start.sh 脚本，脚本指定项目名，在宿主机上的端口以及需要运行的profile，比如这里激活预发环境的profile

```bash
chomd a+x start.sh
./start.sh admin-console 80 preview
``` 