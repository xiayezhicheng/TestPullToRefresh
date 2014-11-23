package com.wangaho.testpulltorefresh.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.wangaho.testpulltorefresh.R;


public  class MainActivity extends FragmentActivity{
	
	private Button[] mTabs;
	private int preposition = 0;
	private int[] ids = { R.id.perimeter_group, R.id.perimeter_rental};
	private GroupFragment groupFragment;
	private RentalFragment rentalFragment;
	private Fragment[] fragments;
	private int index;
	private int currentTabIndex;

	    @Override
	    protected void onCreate(Bundle savedInstanceState) {

	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_main);
	        initTabs();
	    }

	    private void initTabs() {
	    	mTabs = new Button[3];
			mTabs[0] = (Button) findViewById(R.id.perimeter_group);
			mTabs[1] = (Button) findViewById(R.id.perimeter_rental);
			//把第一个tab设为选中状态
			mTabs[0].setSelected(true);
			
			groupFragment = new GroupFragment();
			rentalFragment = new RentalFragment();
			fragments = new Fragment[] {groupFragment, rentalFragment};
			// 添加显示第一个fragment
			getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, groupFragment).
				add(R.id.fragment_container, rentalFragment).hide(rentalFragment).show(groupFragment).commit();
		}

	    /**
		 * button点击事件
		 * @param view
		 */
		public void onTabSelect(View view) {
			switch (view.getId()) {
			case R.id.perimeter_group:
				index = 0;
				break;
			case R.id.perimeter_rental:
				index = 1;
				
				break;
			}
			if (currentTabIndex != index) {
				FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
				trx.hide(fragments[currentTabIndex]);
				if (!fragments[index].isAdded()) {
					trx.add(R.id.fragment_container, fragments[index]);
				}
				trx.show(fragments[index]).commit();
			}
			mTabs[currentTabIndex].setSelected(false);
			//把当前tab设为选中状态
			mTabs[index].setSelected(true);
			currentTabIndex = index;
		}
	    
		@Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        getMenuInflater().inflate(R.menu.demo, menu);
	        return true;
	    }

}

