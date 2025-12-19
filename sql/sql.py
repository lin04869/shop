import pymysql
import pandas as pd
import argparse
import os

# 配置数据库
DB_HOST = os.getenv('DB_HOST', 'localhost')
DB_USER = os.getenv('DB_USER', 'root')
DB_PASS = os.getenv('DB_PASS', '123') #这里改为自己的数据库密码
DB_NAME = os.getenv('DB_NAME', 'sqlnew')

EXCEL_PATH = "data.xlsx"

# 分类规则
TYPE_MAP = {
    "小新": 1,
    "拯救者": 2,
    "ThinkPad": 3,
    "ThinkBook": 3,
    "YOGA": 4,
    "瑞天": 5,
    "来酷": 5
}


# 配置数据库连接
def get_conn():
    conn = pymysql.connect(
        host=DB_HOST,
        user=DB_USER,
        password=DB_PASS,
        database=DB_NAME,
        charset='utf8mb4'
    )
    return conn

# 导入数据
def import_excel(path=EXCEL_PATH, dry=False):
    try:

        df = pd.read_excel(path, sheet_name="Sheet1")
        print("数据条数为", len(df))
        if "商品名称" not in df.columns and "name" not in df.columns:
            print("找不到对应列")
            return False

        conn = get_conn()
        cur = conn.cursor()

        add_num = 0
        upd_num = 0

        for _, row in df.iterrows():
            name = str(row.get("商品名称", row.get("name", ""))).strip()
            if not name:
                continue

            desc = row.get("商品描述", row.get("description", ""))
            desc = str(desc) if pd.notna(desc) else ""

            img = row.get("图片url", row.get("img", ""))
            img = str(img) if pd.notna(img) else ""

            price = row.get("价格", row.get("price", 0))
            price = float(price) if pd.notna(price) else 0.0

            original_price = price  # 原价默认等于售价

            # 是否存在
            cur.execute("SELECT id FROM goods WHERE name=%s AND business_id=1", (name,))
            exist = cur.fetchone()

            if exist:
                # 已有，更新
                if not dry:
                    cur.execute(
                        "UPDATE goods SET description=%s, img=%s, price=%s, original_price=%s WHERE id=%s",
                        (desc, img, price, original_price, exist[0])
                    )
                upd_num += 1
            else:
                # 没有，插入
                if not dry:
                    cur.execute(
                        "INSERT INTO goods (name, description, img, price, original_price, type_id, business_id) "
                        "VALUES (%s, %s, %s, %s, %s, 5, 1)",
                        (name, desc, img, price, original_price)
                    )
                add_num += 1

        if not dry:
            conn.commit()

        print("新增：", add_num, "条；更新：", upd_num, "条")
        return True

    except Exception as e:
        print("导入出错：", e)
        return False


# 更新分类
def update_type(dry=False):
    try:
        conn = get_conn()
        cur = conn.cursor()

        # 查询 type_id 为 5 或没设置的
        cur.execute("SELECT id, name, COALESCE(type_id, 0) FROM goods WHERE type_id=5 OR type_id IS NULL")
        data = cur.fetchall()
        print("待分类的商品数量：", len(data))
        cnt = 0

        for gid, name, old_type in data:
            if not name:
                continue

            new_type = 5
            lower_name = name.lower()

            # 根据关键词判断类别
            for key, tid in TYPE_MAP.items():
                if key.lower() in lower_name:
                    new_type = tid
                    break

            if new_type != old_type:
                if not dry:
                    cur.execute("UPDATE goods SET type_id=%s WHERE id=%s", (new_type, gid))
                cnt += 1

        if not dry:
            conn.commit()

        print("分类更新：", cnt, "条")
        return True

    except Exception as e:
        print("分类更新失败：", e)
        return False


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("--excel", default=EXCEL_PATH)
    parser.add_argument("--dry-run", action="store_true")
    parser.add_argument("--skip-import", action="store_true")
    args = parser.parse_args()

    if not args.skip_import:
        ok = import_excel(args.excel, args.dry_run)
        if not ok:
            print("失败")
            exit()

    update_type(args.dry_run)

    print("ok")
