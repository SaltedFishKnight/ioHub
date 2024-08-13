# ioHub 服务器
1. ioHub 是独立于游戏程序的轻量级游戏服务器
2. 下载 ZIP，解压后将文件夹重命名为 ioHub，将 ioHub 文件夹放入 mods 文件夹下，使用 ioHub\run\launch.bat 来启动服务器
3. 使控制台输出的文字显示彩色
    * WWindows 默认的 CMD 不支持 ANSI Escape Sequences
    * 推荐安装 Microsoft Store 的 Windows Terminal，并将 Windows Terminal 设置为默认启动
    * 不推荐 ANSICON，它可能会导致游戏无法加载
    * 如果你的控制台已经支持 ANSI Escape Sequences，或者你想要其他 ANSI Escape Sequences 支持，则忽略此设置
4. 服务器配置文件路径：ioHub\run\config.txt
    * -Xms2048m：等价于-XX:InitialHeapSize
    * -Xmx2048m：等价于-XX:MaxHeapSize
    * -DexternalPort=50100：ioGate 客户端连接 ioHub 服务器的端口，由于 ioGate 连接的端口范围限制在 [50100, 65535]，若要更改端口，请将端口设置在 [50100, 65535] 之内
    * -DbrokerPort=50000：ioHub 的内部网关端口，一般不需要修改，只要不与 externalPort 和其他服务端口冲突即可
    * -Dfile.encoding=GBK：设置为系统所使用的字符集

***

# ioHub Server
1. ioHub is a lightweight game server independent of the game program
2. Download the ZIP. Unzip it and rename the folder to ioHub. Put the ioHub folder under the mods folder and use ioHub\run\launch.bat to start the server
3. Make the text output from the console appear in color
    * The Windows default CMD does not support ANSI Escape Sequences
    * It is recommended to install Windows Terminal from Microsoft Store and set Windows Terminal as default startup
    * ANSICON is not recommended, it may cause the game can not be loaded
    * If your console already supports ANSI Escape Sequences, or you want other ANSI Escape Sequences supports, then ignore this setting
4. Path to server config file: ioHub\run\config.txt
    * -Xms2048m: equivalent to -XX:InitialHeapSize
    * -Xmx2048m: equivalent to -XX:MaxHeapSize
    * -DexternalPort=50100: the port where ioGate client connects to the ioHub server, since the port range of ioGate connection is limited to [50100, 65535], if you want to change the port, please set the port within [50100, 65535]
    * -DbrokerPort=50000: ioHub's internal gateway port, generally do not need to change, as long as it does not conflict with the externalPort and other service ports
    * -Dfile.encoding=GBK: set to the character set used by the system
