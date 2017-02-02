package com.androidsaper.android.gui;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.SeriesSelection;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.Multisaper.Core.DB.DBConn;
import com.Multisaper.Core.Interfaces.Controller;
import com.androidsaper.R;

import java.sql.SQLException;

public class ChartActivity extends Activity {
    /** Colors to be used for the pie slices. */
    private static int[] COLORS = new int[] { Color.GREEN, Color.BLUE, Color.MAGENTA, Color.CYAN };
    /** The main series that will include all the data. */
    private CategorySeries mSeries = new CategorySeries("");
    /** The main renderer for the main dataset. */
    private DefaultRenderer mRenderer = new DefaultRenderer();
    /** Button for adding entered data to the current series. */
    private GraphicalView mChartView;

    @Override
    protected void onRestoreInstanceState(Bundle savedState) {
        super.onRestoreInstanceState(savedState);
        mSeries = (CategorySeries) savedState.getSerializable("current_series");
        mRenderer = (DefaultRenderer) savedState.getSerializable("current_renderer");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("current_series", mSeries);
        outState.putSerializable("current_renderer", mRenderer);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
      //  mValue = (EditText) findViewById(R.id.xValue);
        mRenderer.setZoomButtonsVisible(true);
        mRenderer.setStartAngle(180);
        mRenderer.setDisplayValues(true);

     //   mAdd = (Button) findViewById(R.id.add);
     //   mAdd.setEnabled(true);
     //   mValue.setEnabled(true);
//
     //   mAdd.setOnClickListener(new View.OnClickListener() {
     //       public void onClick(View v) {
     //           double value = 0;
     //           try {
     //               value = Double.parseDouble(mValue.getText().toString());
     //           } catch (NumberFormatException e) {
     //               mValue.requestFocus();
     //               return;
     //           }
     //           mValue.setText("");
     //           mValue.requestFocus();
     //           mSeries.add("Series " + (mSeries.getItemCount() + 1), value);
     //           SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
     //           renderer.setColor(COLORS[(mSeries.getItemCount() - 1) % COLORS.length]);
     //           mRenderer.addSeriesRenderer(renderer);
     //           mChartView.repaint();
     //       }
     //   });
    }

    private class GetDataTask extends AsyncTask<String, Void, DBConn.PlayeStats> {
        protected  DBConn.PlayeStats doInBackground(String ...args) {
            try {
                DBConn dbc = Controller.getInstance().getDBConnection();
                return dbc.getPlayerStats(Controller.getInstance().getPlayerName());
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(DBConn.PlayeStats ps) {
           //mSeries.clear();
            SimpleSeriesRenderer renderer;

            renderer = new SimpleSeriesRenderer();
            mSeries.add("Deaths", ps.deaths);
            renderer.setColor(COLORS[(mSeries.getItemCount() - 1) % COLORS.length]);
            mRenderer.addSeriesRenderer(renderer);

            renderer = new SimpleSeriesRenderer();
            mSeries.add("Wins", ps.wins);
            renderer.setColor(COLORS[(mSeries.getItemCount() - 1) % COLORS.length]);
            mRenderer.addSeriesRenderer(renderer);

            renderer = new SimpleSeriesRenderer();
            mSeries.add("Unfinished games", ps.games - ps.deaths - ps.wins);
            renderer.setColor(COLORS[(mSeries.getItemCount() - 1) % COLORS.length]);
            mRenderer.addSeriesRenderer(renderer);

            Rebuild();
        }
    }

    void Rebuild() {
       // if(mSeries.getItemCount() == 0)
       //     return;

        mRenderer.setFitLegend(true);
        mRenderer.setLegendTextSize(12);
        mRenderer.setLegendHeight(12);
        ///  mRenderer.setChartTitle("Title");
        //  mRenderer.setChartTitleTextSize(18);

        LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
        mChartView = ChartFactory.getPieChartView(this, mSeries, mRenderer);

        mRenderer.setClickEnabled(true);

        mChartView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SeriesSelection seriesSelection = mChartView.getCurrentSeriesAndPoint();
                if (seriesSelection == null) {
                    Toast.makeText(ChartActivity.this, "No chart element selected", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    for (int i = 0; i < mSeries.getItemCount(); i++) {
                        mRenderer.getSeriesRendererAt(i).setHighlighted(i == seriesSelection.getPointIndex());
                    }
                    mChartView.repaint();
                    Toast.makeText(
                            ChartActivity.this,
                            "Chart data point index " + seriesSelection.getPointIndex() + " selected"
                                    + " point value=" + seriesSelection.getValue(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        layout.addView(mChartView, new LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.FILL_PARENT));

        mChartView.repaint();
    }

    @Override
    protected void onResume() {
        new GetDataTask().execute("");
        super.onResume();
        if (mChartView == null ) {
            Rebuild();
        } else {
            mChartView.repaint();
        }
    }
}
