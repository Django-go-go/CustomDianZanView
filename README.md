# CustomDianZanView #
一个自定义的点赞控件
## 使用的方法很简单: ##
<pre><code>
private DianZanView mDianZanView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDianZanView = (DianZanView) findViewById(R.id.dianzan);
        mDianZanView.initNumber(989);
        mDianZanView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDianZanView.start();
            }
        });

    }
</code></pre>
