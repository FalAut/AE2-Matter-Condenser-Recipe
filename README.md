# AE2 Matter Condenser Recipe

这个模组把 AE2 的物质聚合器改成了配方驱动。  
不再只固定产出物质球/奇点，产物和需求都可以通过配方数据包调整。  
同样的逻辑也支持 [ExtendedAE](https://www.curseforge.com/minecraft/mc-mods/ex-pattern-provider) 的 ME虚空元件。

---

## 物质聚合器

- 可以在 GUI 里直接切换产物。
- `>` 按钮可打开选择窗口，按列表选目标产物。

![Matter Condenser GUI](images/matter_condenser_gui.png)

## 选择窗口

- 产物列表支持滚动，产物多时也能正常浏览。

![Condenser Selector](images/selector_output.png)

## 支持 ME虚空元件

![Void Cell GUI](images/void_cell_compat.png)

## JEI / EMI / REI 适配

- 配方页面已适配，会按当前配方数据展示内容。
- JEI 兼容需要 [AE2 JEI Integration](https://www.curseforge.com/minecraft/mc-mods/ae2-jei-integration) 模组

![Recipe Viewer Compatibility](images/xei_compat.png)

---

## 配方示例

```json
{
  "type": "ae2mcr:condenser",
  "result": {
    "id": "minecraft:iron_ingot",
    "count": 1
  },
  "required_power": 1024
}
```

## 支持 KubeJS 编写配方

```js
ServerEvents.recipes((event) => {
  event.recipes.ae2mcr.condenser('minecraft:diamond', 8192)
})
```