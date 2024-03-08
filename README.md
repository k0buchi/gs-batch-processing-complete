# gs-batch-processing-complete
「[Spring Batch入門ガイド（バッチサービスの作成）](https://spring.pleiades.io/guides/gs/batch-processing/)」を基礎として、追加要件を組み込み、より実用的なバッチプログラムの作成しました。

## プログラムの引数
実行時は、以下の引数を指定してください。
```bash
--spring.batch.job.name=importUserJob
--spring.config.import=optional:classpath:job1.properties,optional:file:job1.properties
--spring.profiles.active=dev
```

## プログラムの解説
[「Spring Batch 入門ガイド」を実用化する](https://zenn.dev/kobuchi/articles/adc96aab508abb)
