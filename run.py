#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
BBS论坛系统自动启动脚本
该脚本处理数据库更新和项目启动
设计用于代码更新后的一键部署

功能:
1. 检查并释放端口8080
2. 检查MySQL服务状态
3. 更新数据库
4. 构建项目
5. 启动应用程序
"""

import os
import sys
import subprocess
import time
import signal
import platform
from pathlib import Path


class BBSAutoStarter:
    def __init__(self):
        self.project_dir = Path(__file__).parent.absolute()
        self.port = 8080
        self.mysql_user = "root"
        self.mysql_password = "123456"
        self.database_name = "bbs"
        self.sql_file = "bbs.sql"
        self.pom_file = "pom.xml"

    def print_header(self):
        """打印启动标题"""
        print("=" * 50)
        print("    BBS论坛系统自动启动")
        print("=" * 50)
        print(f"当前目录: {self.project_dir}")
        print()

    def change_to_project_dir(self):
        """切换到项目目录"""
        os.chdir(self.project_dir)
        print(f"已切换到项目目录: {self.project_dir}")

    def kill_processes_on_port(self):
        """杀死使用指定端口的进程"""
        print(f"检查使用端口{self.port}的进程...")

        if platform.system() == "Windows":
            # Windows系统
            try:
                result = subprocess.run(
                    ["netstat", "-ano"],
                    capture_output=True,
                    text=True
                )
                lines = result.stdout.split('\n')
                pids = []
                for line in lines:
                    if f":{self.port}" in line:
                        parts = line.split()
                        if len(parts) >= 5:
                            pid = parts[-1]
                            if pid.isdigit():
                                pids.append(int(pid))

                if pids:
                    for pid in pids:
                        print(f"杀死进程PID: {pid}")
                        subprocess.run(["taskkill", "/F", "/PID", str(pid)],
                                     capture_output=True)
                        print("进程已杀死.")
                else:
                    print(f"未发现使用端口{self.port}的进程.")

            except Exception as e:
                print(f"检查端口时出错: {e}")
        else:
            # Linux/Unix系统
            try:
                # 使用lsof命令
                result = subprocess.run(
                    ["lsof", "-ti", f":{self.port}"],
                    capture_output=True,
                    text=True
                )
                if result.returncode == 0:
                    pids = result.stdout.strip().split('\n')
                    for pid in pids:
                        if pid.isdigit():
                            print(f"杀死进程PID: {pid}")
                            os.kill(int(pid), signal.SIGKILL)
                            print("进程已杀死.")
                else:
                    print(f"未发现使用端口{self.port}的进程.")
            except FileNotFoundError:
                # 如果没有lsof，使用ss命令
                try:
                    result = subprocess.run(
                        ["ss", "-tulpn"],
                        capture_output=True,
                        text=True
                    )
                    lines = result.stdout.split('\n')
                    for line in lines:
                        if f":{self.port}" in line:
                            # 解析ss输出获取PID
                            parts = line.split()
                            for part in parts:
                                if "pid=" in part:
                                    pid = part.split('=')[1].split(',')[0]
                                    if pid.isdigit():
                                        print(f"杀死进程PID: {pid}")
                                        os.kill(int(pid), signal.SIGKILL)
                                        print("进程已杀死.")
                except Exception as e:
                    print(f"使用ss命令检查端口时出错: {e}")
            except Exception as e:
                print(f"检查端口时出错: {e}")

        print(f"端口{self.port}现在已释放.")
        print()

    def check_mysql_service(self):
        """检查MySQL服务状态"""
        print("[1/4] 检查MySQL服务...")

        try:
            # 检查MySQL客户端是否安装
            result = subprocess.run(
                ["mysql", "--version"],
                capture_output=True,
                text=True
            )
            if result.returncode != 0:
                print("错误: MySQL客户端未安装")
                if platform.system() != "Windows":
                    print("请安装MySQL客户端:")
                    print("sudo apt update && sudo apt install mysql-client")
                return False
        except FileNotFoundError:
            print("错误: MySQL客户端未安装")
            if platform.system() != "Windows":
                print("请安装MySQL客户端:")
                print("sudo apt update && sudo apt install mysql-client")
            return False

        # 测试MySQL连接
        try:
            result = subprocess.run(
                [
                    "mysql",
                    "-u", self.mysql_user,
                    f"-p{self.mysql_password}",
                    "-e", "SELECT 1;"
                ],
                capture_output=True,
                text=True
            )

            if result.returncode == 0:
                print("MySQL服务: 正常")
                return True
            else:
                print("错误: MySQL无法访问")
                print("请确保:")
                print("1. MySQL服务正在运行")
                print(f"2. 用户: {self.mysql_user}, 密码: {self.mysql_password}")
                print("3. MySQL已正确安装")
                if platform.system() != "Windows":
                    print("您可以使用以下命令手动启动MySQL服务:")
                    print("sudo systemctl start mysql")
                return False

        except Exception as e:
            print(f"连接MySQL时出错: {e}")
            return False

    def update_database(self):
        """更新数据库"""
        print()
        print("[2/4] 更新数据库...")

        sql_path = self.project_dir / self.sql_file
        if not sql_path.exists():
            print(f"错误: 在{self.project_dir}中未找到{self.sql_file}")
            print("请确保您在项目目录中运行此脚本")
            return False

        print(f"找到{self.sql_file}，正在更新数据库...")

        try:
            with open(sql_path, 'r', encoding='utf-8') as f:
                result = subprocess.run(
                    [
                        "mysql",
                        "-u", self.mysql_user,
                        f"-p{self.mysql_password}",
                        f"--default-character-set=utf8mb4"
                    ],
                    stdin=f,
                    capture_output=True,
                    text=True
                )

            if result.returncode == 0:
                print("数据库更新: 成功")
                return True
            else:
                print("数据库更新: 失败")
                print("请检查sql文件是否有语法错误")
                print(f"错误信息: {result.stderr}")
                return False

        except Exception as e:
            print(f"更新数据库时出错: {e}")
            return False

    def check_build_tools(self):
        """检查构建工具"""
        print()
        print("[3/4] 检查构建工具...")

        # 检查pom.xml文件
        pom_path = self.project_dir / self.pom_file
        if not pom_path.exists():
            print(f"错误: 在{self.project_dir}中未找到{self.pom_file}")
            print("请确保您在项目目录中运行此脚本")
            return False

        # 检查Java
        try:
            result = subprocess.run(
                ["java", "-version"],
                capture_output=True,
                text=True
            )
            if result.returncode != 0:
                print("错误: Java未安装或配置不正确")
                if platform.system() != "Windows":
                    print("请安装Java 8或更高版本:")
                    print("sudo apt update && sudo apt install openjdk-8-jdk")
                return False
        except FileNotFoundError:
            print("错误: Java未安装")
            if platform.system() != "Windows":
                print("请安装Java 8或更高版本:")
                print("sudo apt update && sudo apt install openjdk-8-jdk")
            return False

        print("构建工具检查: 正常")
        return True

    def build_project(self):
        """构建项目"""
        print()
        print("[3/4] 构建项目...")
        print("这可能需要一点时间，请稍候...")

        try:
            # 在Windows上使用完整路径或mvn.cmd
            if platform.system() == "Windows":
                mvn_cmd = "mvn.cmd"
            else:
                mvn_cmd = "mvn"

            result = subprocess.run(
                [mvn_cmd, "clean", "compile", "-q"],
                capture_output=True,
                text=True,
                cwd=self.project_dir
            )

            if result.returncode == 0:
                print("构建: 成功")
                return True
            else:
                print("构建: 失败")
                print("请检查:")
                print("1. Maven已安装且在PATH环境变量中")
                print("2. JDK已正确配置")
                print("3. 源代码中没有编译错误")
                print("4. 网络连接正常以下载依赖")
                print(f"错误信息: {result.stderr}")
                return False

        except FileNotFoundError:
            print("错误: 找不到mvn命令")
            print("请确保Maven已正确安装并添加到PATH环境变量")
            print("或者尝试手动运行: mvn clean compile")
            return False
        except Exception as e:
            print(f"构建项目时出错: {e}")
            return False

    def start_application(self):
        """启动应用程序"""
        print()
        print("[4/4] 启动应用程序...")
        print("=" * 50)
        print("BBS论坛系统正在启动...")
        print("=" * 50)
        print()
        print(f"访问应用程序: http://localhost:{self.port}")
        print("默认账户:")
        print("- 管理员: admin / 123456")
        print("- 用户: u1, u2, u3 / 123456")
        print()
        print("按Ctrl+C停止应用程序")
        print("重启方法: 关闭此窗口并重新运行run.py")
        print()
        print("注意: 数据库中的外键约束已被移除")
        print("数据完整性现在由应用程序级别维护")
        print()

        try:
            print("正在启动Spring Boot应用程序...")
            # 使用Popen以便能够捕获Ctrl+C信号
            # 在Windows上使用完整路径或mvn.cmd
            if platform.system() == "Windows":
                mvn_cmd = "mvn.cmd"
            else:
                mvn_cmd = "mvn"

            process = subprocess.Popen(
                [mvn_cmd, "spring-boot:run"],
                stdout=sys.stdout,
                stderr=sys.stderr,
                text=True,
                cwd=self.project_dir
            )

            # 等待进程结束
            try:
                process.wait()
            except KeyboardInterrupt:
                print("\n接收到停止信号，正在停止应用程序...")
                process.terminate()
                try:
                    process.wait(timeout=10)
                except subprocess.TimeoutExpired:
                    process.kill()
                    print("应用程序已强制停止")
                else:
                    print("应用程序已正常停止")

            print("应用程序已停止.")
            print("您现在可以关闭此窗口.")

        except Exception as e:
            print(f"启动应用程序时出错: {e}")
            print("检查端口8080是否可用")
            print("或检查应用程序日志中的错误")
            return False

        return True

    def run(self):
        """运行完整的启动流程"""
        try:
            # 打印标题
            self.print_header()

            # 切换到项目目录
            self.change_to_project_dir()

            # 杀死使用端口的进程
            self.kill_processes_on_port()

            # 检查MySQL服务
            if not self.check_mysql_service():
                print("MySQL服务检查失败，请解决问题后重试")
                return False

            # 更新数据库
            if not self.update_database():
                print("数据库更新失败，请解决问题后重试")
                return False

            # 检查构建工具
            if not self.check_build_tools():
                print("构建工具检查失败，请解决问题后重试")
                return False

            # 构建项目
            if not self.build_project():
                print("项目构建失败，请解决问题后重试")
                return False

            # 启动应用程序
            self.start_application()

            return True

        except KeyboardInterrupt:
            print("\n用户中断，程序退出")
            return False
        except Exception as e:
            print(f"运行过程中发生错误: {e}")
            return False


def main():
    """主函数"""
    # 检查Python版本
    if sys.version_info < (3, 6):
        print("错误: 需要Python 3.6或更高版本")
        sys.exit(1)

    # 创建启动器实例并运行
    starter = BBSAutoStarter()
    success = starter.run()

    if not success:
        print("启动过程中发生错误，程序退出")
        sys.exit(1)
    else:
        print("程序执行完成")


if __name__ == "__main__":
    main()