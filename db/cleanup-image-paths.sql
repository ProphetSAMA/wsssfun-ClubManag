-- 图片字段清洗脚本
-- ---------------------------------------------------------------------------
-- 背景：早期上传组件用 process.env.BASE_API 拼 URL，而 Vite 只暴露 VITE_ 前缀变量，
-- 于是库里留下了两种脏数据：
--   1. undefined/images/xxx.png                  （前缀被拼成了字符串 "undefined"）
--   2. http://localhost:8888/images/xxx.png      （写死了本机地址和端口）
-- 前端 src/utils/image.ts 的 imgUrl() 目前兜着这两种格式，但数据本身应该干净。
--
-- 本脚本只处理这两种情况，把 image 统一成 /images/xxx.png：
--   * 只删 "undefined" 片段
--   * 只删 localhost / 127.0.0.1 这类本机前缀
--   * 其它绝对地址（例如真正的 CDN 域名）保持原样，不碰
--   * 逗号分隔的多图会逐个片段一起清洗
--
-- 只改 team / news 的 image 字段，不动其它列；末尾附回滚 SQL。
-- 建议先在测试库跑一遍：
--   mysql -u root -p club < db/cleanup-image-paths.sql
-- ---------------------------------------------------------------------------

USE `club`;

-- 0. 有多少行需要处理
SELECT 'team' AS tbl, COUNT(*) AS dirty_rows
FROM team
WHERE image IS NOT NULL AND TRIM(image) <> ''
  AND (image LIKE '%undefined%' OR image REGEXP 'https?://(localhost|127\\.0\\.0\\.1)')
UNION ALL
SELECT 'news', COUNT(*)
FROM news
WHERE image IS NOT NULL AND TRIM(image) <> ''
  AND (image LIKE '%undefined%' OR image REGEXP 'https?://(localhost|127\\.0\\.0\\.1)');

-- 1. 备份原始值
DROP TABLE IF EXISTS team_image_backup;
CREATE TABLE team_image_backup AS SELECT id, image FROM team;
DROP TABLE IF EXISTS news_image_backup;
CREATE TABLE news_image_backup AS SELECT id, image FROM news;

-- 2. 清洗
UPDATE team
SET image = CONCAT('/', TRIM(BOTH '/' FROM REPLACE(
        REGEXP_REPLACE(REPLACE(image, 'undefined', ''),
                       'https?://(localhost|127\\.0\\.0\\.1)(:[0-9]+)?', ''),
        '//', '/')))
WHERE image IS NOT NULL AND TRIM(image) <> ''
  AND (image LIKE '%undefined%' OR image REGEXP 'https?://(localhost|127\\.0\\.0\\.1)');

UPDATE news
SET image = CONCAT('/', TRIM(BOTH '/' FROM REPLACE(
        REGEXP_REPLACE(REPLACE(image, 'undefined', ''),
                       'https?://(localhost|127\\.0\\.0\\.1)(:[0-9]+)?', ''),
        '//', '/')))
WHERE image IS NOT NULL AND TRIM(image) <> ''
  AND (image LIKE '%undefined%' OR image REGEXP 'https?://(localhost|127\\.0\\.0\\.1)');

-- 3. 校验：下面两条应返回 0
SELECT COUNT(*) AS team_still_dirty FROM team
WHERE image IS NOT NULL AND TRIM(image) <> ''
  AND (image LIKE '%undefined%' OR image REGEXP 'https?://(localhost|127\\.0\\.0\\.1)');
SELECT COUNT(*) AS news_still_dirty FROM news
WHERE image IS NOT NULL AND TRIM(image) <> ''
  AND (image LIKE '%undefined%' OR image REGEXP 'https?://(localhost|127\\.0\\.0\\.1)');

-- 4. 结果预览
SELECT id, name, image FROM team;
SELECT id, title, image FROM news;

-- ---------------------------------------------------------------------------
-- 回滚：
--   UPDATE team t JOIN team_image_backup b ON t.id = b.id SET t.image = b.image;
--   UPDATE news n JOIN news_image_backup b ON n.id = b.id SET n.image = b.image;
--   DROP TABLE team_image_backup;
--   DROP TABLE news_image_backup;
-- ---------------------------------------------------------------------------
