# Changelog

## [1.0.2] - 2026-08-05

### English

- Fixed a crash when DataEnergistics is not installed: the condenser mixin no longer hard-references DataEnergistics classes and now calls its accessor via reflection, so the mod loads without it.

### 中文

- 修复了未安装 DataEnergistics 时模组崩溃的问题：物质聚合器 mixin 不再硬引用 DataEnergistics 的类，改为通过反射调用其访问器，模组现在可以在没有 DataEnergistics 的情况下正常加载。

## [1.0.1] - 2026-06-07

### English

- Added support for DataEnergistics Data Capture Ball recipes in the Matter Condenser.
- Condenser recipes can now define their own catalyst items more clearly, making modpack customization more flexible.

### 中文

- 新增了对 Data Energistics 数据采集球配方的支持。
- 物质聚合器配方现在可以更清楚地指定各自使用的催化物，整合包和数据包自定义会更灵活。

## [1.0.0] - 2026-05-18

### English

- Initial release.

### 中文

- 首次发布。
